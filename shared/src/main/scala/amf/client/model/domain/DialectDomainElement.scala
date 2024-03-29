package amf.client.model.domain

import amf.client.convert.VocabulariesClientConverter._
import amf.core.model.BoolField
import amf.core.model.domain.AmfArray
import amf.core.parser.Value
import amf.core.vocabulary.Namespace
import amf.plugins.document.vocabularies.model.domain.{DialectDomainElement => InternalDialectDomainElement}
import org.yaml.model.{YMapEntry, YNode}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
@JSExportAll
case class DialectDomainElement(override private[amf] val _internal: InternalDialectDomainElement)
    extends DomainElement {

  @JSExportTopLevel("model.domain.DialectDomainElement")
  def this() = this(InternalDialectDomainElement())

  def isAbstract(): BoolField = _internal.isAbstract
  def withAbstract(isAbstract: Boolean): DialectDomainElement = {
    _internal.withAbstract(isAbstract)
    this
  }

  def withInstanceTypes(types: ClientList[String]): DialectDomainElement = {
    _internal.withInstanceTypes(types.asInternal)
    this
  }

  def withDefinedby(nodeMapping: NodeMapping): DialectDomainElement = {
    _internal.withDefinedBy(nodeMapping._internal)
    this
  }

  def definedBy(): NodeMapping = NodeMapping(_internal.definedBy)

  def localRefName(): String = _internal.localRefName

  def includeName(): String = _internal.includeName

  def setObjectProperty(propertyId: String, value: DialectDomainElement): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setObjectField(mapping, value._internal, YNode.Empty)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def setObjectCollectionProperty(propertyId: String,
                                  value: ClientList[DialectDomainElement]): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setObjectField(mapping, value.asInternal, YNode.Empty)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def getTypeUris(): ClientList[String] = _internal.meta.`type`.map(_.iri()).asClient

  def getPropertyUris(): ClientList[String] = _internal.meta.fields.map(_.value.iri()).asClient

  def getScalarByPropertyUri(propertyId: String): ClientList[Any] = {
    val expanded = Namespace.expand(propertyId).iri()
    val res: Seq[Any] = _internal.findPropertyMappingByTermPropertyId(expanded).map(_.toField) match {
      case Some(mapping) =>
        _internal.fields.getValueAsOption(mapping) match {
          case Some(res: Seq[_]) => res
          case Some(value)       => Seq(value)
          case None =>
            _internal.fields.getValueAsOption(mapping).map(Seq(_)).getOrElse(Nil)
        }
      case _ =>
        Nil
    }
    res.asClient
  }

  def getObjectPropertyUri(propertyId: String): ClientList[DialectDomainElement] = {
    val expanded = Namespace.expand(propertyId).iri()
    val res: Seq[InternalDialectDomainElement] =
      _internal.findPropertyMappingByTermPropertyId(expanded).map(_.toField) match {
        case Some(f) =>
          _internal.fields.getValueAsOption(f) match {
            case Some(Value(arr:AmfArray,_)) => arr.values.collect({case d:InternalDialectDomainElement => d})
            case Some(Value(value: InternalDialectDomainElement,_)) => Seq(DialectDomainElement(value))
            case None => Nil

          }
        case _ => Nil
      }
    res.asClient
  }

  private def emptyEntry: YMapEntry = YMapEntry(YNode.Empty, YNode.Empty)
  def setLiteralProperty(propertyId: String, value: String): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setProperty(mapping, value, emptyEntry)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def setLiteralProperty(propertyId: String, value: Int): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setProperty(mapping, value, emptyEntry)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def setLiteralProperty(propertyId: String, value: Double): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setProperty(mapping, value, emptyEntry)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def setLiteralProperty(propertyId: String, value: Float): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setProperty(mapping, value, emptyEntry)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def setLiteralProperty(propertyId: String, value: Boolean): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setProperty(mapping, value, emptyEntry)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }

  def setLiteralProperty(propertyId: String, value: ClientList[Any]): InternalDialectDomainElement = {
    _internal.findPropertyMappingByTermPropertyId(Namespace.expand(propertyId).iri()) match {
      case Some(mapping) =>
        _internal.setProperty(mapping, value.asInternal, emptyEntry)
      case None =>
        throw new Exception(s"Cannot find node mapping for propertyId $propertyId")
    }
  }
}
