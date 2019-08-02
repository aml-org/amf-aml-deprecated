package amf.plugins.document.vocabularies

import amf.core.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.metamodel.{Field, Obj, Type}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.{Annotations, DefaultParserSideErrorHandler}
import amf.core.registries.AMFDomainEntityResolver
import amf.core.remote.{Aml, Cache, Context}
import amf.core.services.{RuntimeCompiler, RuntimeValidator}
import amf.core.unsafe.PlatformSecrets
import amf.core.validation.core.ValidationProfile
import amf.core.vocabulary.ValueType
import amf.internal.environment.Environment
import amf.internal.resource.StringResourceLoader
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstance, DialectInstanceFragment, DialectInstanceTrait}
import amf.plugins.document.vocabularies.model.domain.{DialectDomainElement, NodeMapping, ObjectMapProperty}
import amf.plugins.document.vocabularies.resolution.pipelines.DialectResolutionPipeline

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DialectsRegistry extends AMFDomainEntityResolver with PlatformSecrets {

  protected var map: Map[String, Dialect]                               = Map()
  protected var resolved: Map[String, Boolean]                          = Map()

  private[vocabularies] var validations: Map[String, ValidationProfile] = Map()

  def findNode(dialectNode: String): Option[(Dialect, NodeMapping)] = {
    map.values.find(dialect => dialectNode.contains(dialect.id)) map { dialect =>
      (dialect, dialect.declares.find(_.id == dialectNode))
    } collectFirst { case (dialect, Some(nodeMapping: NodeMapping)) => (dialect, nodeMapping) }
  }

  def knowsHeader(header: String): Boolean = {
    header == "%Vocabulary 1.0" || header == "%Dialect 1.0" || header == "%Library / Dialect 1.0" || map
      .contains(headerKey(header))
  }

  def knowsDialectInstance(instance: DialectInstance): Boolean = dialectFor(instance).isDefined

  def dialectFor(instance: DialectInstanceTrait): Option[Dialect] =
    instance.definedBy().option().flatMap(id => map.values.find(_.id == id))

  def allDialects(): Seq[Dialect] = map.values.toSeq

  def register(dialect: Dialect): DialectsRegistry = {
    dialect.allHeaders foreach { header =>
      map += (header -> dialect)
      resolved -= header
    }
    validations -= dialect.nameAndVersion()
    this
  }

  def findDialectForHeader(header:String):Option[Dialect] = {
    val dialectId = headerKey(header.split("\\|").head)
    map.get(dialectId).filter(d => !d.documents().keyProperty().value())
  }
  /**
    * Finds and resolve if unresolved a resolved dialect, the output of this invocation is a dialect ready to parse
    * @param header
    * @param k
    * @return
    */

  def dialectById(id:String):Option[Dialect] = map.values.find(_.id == id)

  def withRegisteredDialect(header: String)(k: Dialect => Option[DialectInstanceTrait]): Option[DialectInstanceTrait] = {
    val dialectId = headerKey(header.split("\\|").head)
    map.get(dialectId) match {
      case Some(dialect) => withRegisteredDialect(dialect)(k)
      case _ => None
    }
  }

  def withRegisteredDialect(dialect: Dialect)(k: Dialect => Option[DialectInstanceTrait]): Option[DialectInstanceTrait] = {

    if (!resolved.getOrElse(dialect.id, false)) {
      val resolvedDialect = new DialectResolutionPipeline(DefaultParserSideErrorHandler(dialect)).resolve(dialect)
      resolved += (dialect.id -> true)
      map += (dialect.id      -> resolvedDialect)
      k(resolvedDialect)
    } else {
      k(dialect)
    }
  }

  protected def headerKey(header: String): String = header.trim.replace(" ", "")

  override def findType(typeString: String): Option[Obj] = {
    val foundMapping: Option[(Dialect, DomainElement)] = map.values.toSeq.distinct
      .collect {
        case dialect: Dialect =>
          dialect.declares.find {
            case nodeMapping: NodeMapping => nodeMapping.id == typeString
            case _                        => false
          } map { nodeMapping =>
            (dialect, nodeMapping)
          }
      }
      .collectFirst { case Some(x) => x }

    foundMapping match {
      case Some((dialect: Dialect, nodeMapping: NodeMapping)) =>
        Some(buildMetaModel(nodeMapping, dialect))
      case _ => None
    }
  }

  override def buildType(modelType: Obj): Option[Annotations => AmfObject] = modelType match {
    case dialectModel: DialectDomainElementModel =>
      val reviver = (annotations: Annotations) =>
        dialectModel.nodeMapping match {
          case Some(nodeMapping) =>
            DialectDomainElement(annotations)
              .withInstanceTypes(dialectModel.typeIri :+ nodeMapping.id)
              .withDefinedBy(nodeMapping)
          case _ =>
            throw new Exception(s"Cannot find node mapping for dialectModel $dialectModel")
      }

      Some(reviver)
    case _ => None
  }

  def buildMetaModel(nodeMapping: NodeMapping, dialect: Dialect): DialectDomainElementModel = {
    val nodeType = nodeMapping.nodetypeMapping
    val fields   = nodeMapping.propertiesMapping().map(_.toField)
    val mapPropertiesInDomain = dialect.declares
      .collect {
        case nodeMapping: NodeMapping =>
          nodeMapping.propertiesMapping().filter(_.classification() == ObjectMapProperty)
      }
      .flatten
      .filter(prop => prop.objectRange().exists(_.value() == nodeMapping.id))

    val mapPropertiesFields =
      mapPropertiesInDomain
        .map(_.mapTermKeyProperty())
        .distinct
        .map(iri => Field(Type.Str, ValueType(iri.value()), ModelDoc(ModelVocabularies.Parser, "custom", iri.value())))

    new DialectDomainElementModel(Seq(nodeType.value()), fields ++ mapPropertiesFields, Some(nodeMapping))
  }

  def resolveRegisteredDialect(header: String): Unit = {
    val dialectId = headerKey(header.split("\\|").head)
    map.get(dialectId) match {
      case Some(dialect) =>
        if (!resolved.getOrElse(dialectId, false)) {
          val resolvedDialect = new DialectResolutionPipeline(DefaultParserSideErrorHandler(dialect)).resolve(dialect)
          resolved += (dialectId -> true)
          map += (dialectId      -> resolvedDialect)
        }
      case _ =>
        throw new Exception(s"Cannot find register Dialect with header '$header'")
    }
  }

  def registerDialect(uri: String, environment: Environment = Environment()): Future[Dialect] = {
    map.get(uri) match {
      case Some(dialect) => Future { dialect }
      case _ =>
        RuntimeValidator.disableValidationsAsync() { reenable =>
          RuntimeCompiler(uri,
                          Some("application/yaml"),
                          Some(Aml.name),
                          Context(platform),
                          env = environment,
                          cache = Cache())
            .map {
              case dialect: Dialect =>
                reenable()
                register(dialect)
                dialect
            }
        }
    }
  }

  def unregisterDialect(uri: String): Unit = {
    map.foreach {
      case (header, dialect) =>
        if (dialect.id == uri) {
          map -= header
          validations -= dialect.nameAndVersion()
        }
    }
  }

  def registerDialect(url: String, code: String): Future[Dialect] = registerDialect(url, code, Environment())

  def registerDialect(url: String, code: String, env: Environment): Future[Dialect] =
    registerDialect(url, env.add(StringResourceLoader(url, code)))
}
