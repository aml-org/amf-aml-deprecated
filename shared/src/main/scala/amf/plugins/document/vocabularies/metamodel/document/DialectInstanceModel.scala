package amf.plugins.document.vocabularies.metamodel.document

import amf.core.metamodel.Field
import amf.core.metamodel.Type.{Array, Iri, Str}
import amf.core.metamodel.document._
import amf.core.model.domain.AmfObject
import amf.core.vocabulary.{Namespace, ValueType}
import amf.plugins.document.vocabularies.model.document.{DialectInstance, DialectInstanceFragment, DialectInstanceLibrary, DialectInstancePatch}

object DialectInstanceModel extends DocumentModel with ExternalContextModel {

  val DefinedBy = Field(Iri, Namespace.Meta + "definedBy")
  val GraphDependencies = Field(Array(Iri), Namespace.Document + "graphDependencies")

  override def modelInstance: AmfObject = DialectInstance()

  override val `type`: List[ValueType] =
    Namespace.Meta + "DialectInstance" :: DocumentModel.`type`

  override val fields: List[Field] = DefinedBy :: GraphDependencies :: Externals :: DocumentModel.fields
}

object DialectInstanceLibraryModel extends DocumentModel with ExternalContextModel {

  val DefinedBy = Field(Iri, Namespace.Meta + "definedBy")
  val GraphDependencies = Field(Array(Iri), Namespace.Document + "graphDependencies")

  override def modelInstance: AmfObject = DialectInstanceLibrary()

  override val `type`: List[ValueType] =
    Namespace.Meta + "DialectInstanceLibrary" :: ModuleModel.`type`

  override val fields: List[Field] = DefinedBy :: GraphDependencies :: Externals :: ModuleModel.fields
}

object DialectInstanceFragmentModel extends DocumentModel with ExternalContextModel {

  val DefinedBy = Field(Iri, Namespace.Meta + "definedBy")
  val Fragment = Field(Str, Namespace.Meta + "fragment")
  val GraphDependencies = Field(Array(Iri), Namespace.Document + "graphDependencies")

  override def modelInstance: AmfObject = DialectInstanceFragment()

  override val `type`: List[ValueType] =
    Namespace.Meta + "DialectInstanceFragment" :: FragmentModel.`type`

  override val fields: List[Field] = DefinedBy :: Fragment :: GraphDependencies :: Externals :: FragmentModel.fields
}

object DialectInstancePatchModel extends DocumentModel with ExternalContextModel with ExtensionLikeModel {
  val DefinedBy = Field(Iri, Namespace.Meta + "definedBy")
  val GraphDependencies = Field(Array(Iri), Namespace.Document + "graphDependencies")

  override def modelInstance: AmfObject = DialectInstancePatch()

  override val `type`: List[ValueType] =
    Namespace.Meta + "DialectInstancePatch" :: Namespace.Document + "DocumentExtension" :: BaseUnitModel.`type`

  override val fields: List[Field] = DefinedBy :: GraphDependencies :: Externals :: Extends :: FragmentModel.fields
}