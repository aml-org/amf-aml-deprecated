package amf.plugins.document.vocabularies

import amf.client.parse.DefaultErrorHandler
import amf.core.CompilerContextBuilder
import amf.core.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.metamodel.{Field, Obj, Type}
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.Annotations
import amf.core.parser.errorhandler.UnhandledParserErrorHandler
import amf.core.registries.AMFDomainEntityResolver
import amf.core.remote.Aml
import amf.core.services.RuntimeCompiler
import amf.core.unsafe.PlatformSecrets
import amf.core.validation.core.ValidationProfile
import amf.core.vocabulary.ValueType
import amf.internal.environment.Environment
import amf.internal.resource.StringResourceLoader
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstanceUnit}
import amf.plugins.document.vocabularies.model.domain.{DialectDomainElement, NodeMapping, ObjectMapProperty}
import amf.plugins.document.vocabularies.resolution.pipelines.DialectResolutionPipeline
import org.mulesoft.common.core._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DialectsRegistry extends AMFDomainEntityResolver with PlatformSecrets {

  protected var map: Map[String, Dialect] = Map()
  protected var resolved: Set[String]     = Set()

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

  def knowsDialectInstance(instance: DialectInstanceUnit): Boolean = dialectFor(instance).isDefined

  def dialectFor(instance: DialectInstanceUnit): Option[Dialect] =
    instance.definedBy().option().flatMap(id => map.values.find(_.id == id))

  def allDialects(): Iterable[Dialect] = map.values

  def register(dialect: Dialect): DialectsRegistry = {
    dialect.allHeaders foreach { header =>
      map += (header -> dialect)
    }
    resolved -= dialect.header
    validations -= dialect.header
    this
  }

  def findDialectForHeader(header: String): Option[Dialect] = map.get(header)

  def dialectById(id: String): Option[Dialect] = map.values.find(_.id == id)

  def withRegisteredDialect(header: String)(k: Dialect => Option[DialectInstanceUnit]): Option[DialectInstanceUnit] = {
    val dialectId = headerKey(header.split("\\|").head)
    map.get(dialectId) match {
      case Some(dialect) => withRegisteredDialect(dialect)(k)
      case _             => None
    }
  }

  def withRegisteredDialect(dialect: Dialect)(
      fn: Dialect => Option[DialectInstanceUnit]): Option[DialectInstanceUnit] = {
    if (!resolved.contains(dialect.header))
      fn(resolveDialect(dialect))
    else
      fn(dialect)
  }

  private def resolveDialect(dialect: Dialect) = {
    val solved = new DialectResolutionPipeline(DefaultErrorHandler()).resolve(dialect)
    dialect.allHeaders foreach { header =>
      map += (header -> solved)
    }
    resolved += dialect.header
    solved
  }

  protected def headerKey(header: String): String = header.stripSpaces

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

    val nodeTypes = nodeType.option().map(Seq(_)).getOrElse(Nil)
    new DialectDomainElementModel(nodeTypes, fields ++ mapPropertiesFields, Some(nodeMapping))
  }

  def resolveRegisteredDialect(header: String): Unit = {
    val h = headerKey(header.split("\\|").head)
    map.get(h) match {
      case Some(dialect) => resolveDialect(dialect)
      case _             => throw new Exception(s"Cannot find Dialect with header '$header'")
    }
  }

  def registerDialect(uri: String, environment: Environment = Environment()): Future[Dialect] = {
    map.get(uri) match {
      case Some(dialect) => Future { dialect }
      case _ =>
        val context = new CompilerContextBuilder(uri, platform, UnhandledParserErrorHandler).withEnvironment(environment).build()
        RuntimeCompiler.forContext(context,
                        Some("application/yaml"),
                        Some(Aml.name))
          .map {
            case dialect: Dialect =>
              register(dialect)
              dialect
          }
    }

  }

  def unregisterDialect(uri: String): Unit = {
    map.foreach {
      case (header, dialect) =>
        if (dialect.id == uri) {
          map -= header
          validations -= dialect.header
          resolved -= dialect.header
        }
    }
  }

  def registerDialect(url: String, code: String): Future[Dialect] = registerDialect(url, code, Environment())

  def registerDialect(url: String, code: String, env: Environment): Future[Dialect] =
    registerDialect(url, env.add(StringResourceLoader(url, code)))

  def remove(uri: String): Unit = {
    val headers = map.filter(_._2.id == uri).keys.toList
    resolved = resolved.filter(l => !headers.contains(l))
    map = map.filter(_._2.id != uri)
  }
}
