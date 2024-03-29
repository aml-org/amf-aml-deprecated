package amf.plugins.document.vocabularies.metamodel.domain

import amf.core.metamodel.Field
import amf.core.metamodel.Type.{Array, Iri, Str}
import amf.core.metamodel.domain.{DomainElementModel, ExternalModelVocabularies, ModelDoc, ModelVocabularies}
import amf.core.model.domain.AmfObject
import amf.core.vocabulary.{Namespace, ValueType}
import amf.plugins.document.vocabularies.model.domain.{DatatypePropertyTerm, ObjectPropertyTerm}

abstract class PropertyTermModel extends DomainElementModel {
  val Name = Field(Str, Namespace.Core + "name",
    ModelDoc(ModelVocabularies.Core, "name", "Name of the property term"))
  val DisplayName = Field(Str, Namespace.Core + "displayName",
    ModelDoc(ModelVocabularies.Core, "display name", "Human readable name for the property term"))
  val Description = Field(Str, Namespace.Core + "description",
    ModelDoc(ModelVocabularies.Core, "description", "Human readable description of the property term"))
  val Range       = Field(Iri, Namespace.Rdfs + "range",
    ModelDoc(ExternalModelVocabularies.Rdfs, "range", "Range of the proeprty term, scalar or object"))
  val SubPropertyOf  = Field(Array(Iri), Namespace.Rdfs + "subPropertyOf",
    ModelDoc(ExternalModelVocabularies.Rdfs, "subPropertyOf", "Subsumption relationship for terms"))

  override def fields: List[Field] = DisplayName :: Description :: Range :: SubPropertyOf :: DomainElementModel.fields
}


object ObjectPropertyTermModel extends PropertyTermModel {
  override val `type`: List[ValueType] = Namespace.Owl + "ObjectProperty" :: Namespace.Meta + "Property" :: DomainElementModel.`type`
  override def modelInstance: AmfObject = ObjectPropertyTerm()
}

object DatatypePropertyTermModel extends PropertyTermModel {
  override val `type`: List[ValueType] = Namespace.Owl + "DatatypeProperty" :: Namespace.Meta + "Property" :: DomainElementModel.`type`
  override def modelInstance: AmfObject = DatatypePropertyTerm()
}