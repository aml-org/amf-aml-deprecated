package amf.dialects

import amf.core.remote._

import scala.concurrent.ExecutionContext

class DialectsParsingTest extends DialectTests {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val basePath = "shared/src/test/resources/vocabularies2/dialects/"

  test("parse 1 test") {
    init().flatMap(_ => cycle("example1.raml", "example1.json", VocabularyYamlHint, Amf))
  }

  test("parse 1b test") {
    init().flatMap(_ => cycle("example1b.raml", "example1b.json", VocabularyYamlHint, Amf))
  }

  test("parse 2 test") {
    init().flatMap(_ => cycle("example2.raml", "example2.json", VocabularyYamlHint, Amf))
  }

  test("parse 3 test") {
    init().flatMap(_ => cycle("example3.raml", "example3.json", VocabularyYamlHint, Amf))
  }

  test("parse 4 test") {
    init().flatMap(_ => cycle("example4.raml", "example4.json", VocabularyYamlHint, Amf))
  }

  test("parse 5 test") {
    init().flatMap(_ => cycle("example5.raml", "example5.json", VocabularyYamlHint, Amf))
  }

  test("parse 6 test") {
    init().flatMap(_ => cycle("example6.raml", "example6.json", VocabularyYamlHint, Amf))
  }

  test("parse 7 test") {
    init().flatMap(_ => cycle("example7.raml", "example7.json", VocabularyYamlHint, Amf))
  }

  test("parse 8 test") {
    init().flatMap(_ => cycle("example8.raml", "example8.json", VocabularyYamlHint, Amf))
  }

  test("parse 9 test") {
    init().flatMap(_ => cycle("example9.raml", "example9.json", VocabularyYamlHint, Amf))
  }

  test("parse 10 test") {
    init().flatMap(_ => cycle("example10.raml", "example10.json", VocabularyYamlHint, Amf))
  }

  test("parse 11 test") {
    init().flatMap(_ => cycle("example11.raml", "example11.json", VocabularyYamlHint, Amf))
  }

  test("parse 12 test") {
    init().flatMap(_ => cycle("example12.raml", "example12.json", VocabularyYamlHint, Amf))
  }

  test("parse 13 test") {
    init().flatMap(_ => cycle("example13.raml", "example13.json", VocabularyYamlHint, Amf))
  }

  test("parse 14 test") {
    init().flatMap(_ => cycle("example14.raml", "example14.json", VocabularyYamlHint, Amf))
  }

  test("parse 15 test") {
    init().flatMap(_ => cycle("example15.raml", "example15.json", VocabularyYamlHint, Amf))
  }

  test("parse 16 test") {
    init().flatMap(_ => cycle("example16.raml", "example16.json", VocabularyYamlHint, Amf))
  }

  test("parse 17 test") {
    init().flatMap(_ => cycle("example17.raml", "example17.json", VocabularyYamlHint, Amf))
  }

  test("parse 18 test") {
    init().flatMap(_ => cycle("example18.raml", "example18.json", VocabularyYamlHint, Amf))
  }

  test("parse 19 test") {
    init().flatMap(_ => cycle("example19.raml", "example19.json", VocabularyYamlHint, Amf))
  }

  test("parse 20 test") {
    init().flatMap(_ => cycle("example20.raml", "example20.json", VocabularyYamlHint, Amf))
  }

  test("parse 21 test") {
    init().flatMap(_ => cycle("example21.raml", "example21.json", VocabularyYamlHint, Amf))
  }

  test("parse mappings_lib test") {
    init().flatMap(_ => cycle("mappings_lib.raml", "mappings_lib.json", VocabularyYamlHint, Amf))
  }

  test("generate 1 test") {
    init().flatMap(_ => cycle("example1.json", "example1.raml", AmfJsonHint, Aml))
  }

  test("generate 2 test") {
    init().flatMap(_ => cycle("example2.json", "example2.raml", AmfJsonHint, Aml))
  }

  test("generate 3 test") {
    init().flatMap(_ => cycle("example3.json", "example3.raml", AmfJsonHint, Aml))
  }

  test("generate 4 test") {
    init().flatMap(_ => cycle("example4.json", "example4.raml", AmfJsonHint, Aml))
  }

  test("generate 5 test") {
    init().flatMap(_ => cycle("example5.json", "example5.raml", AmfJsonHint, Aml))
  }

  test("generate 6 test") {
    init().flatMap(_ => cycle("example6.json", "example6.raml", AmfJsonHint, Aml))
  }

  test("generate 7 test") {
    init().flatMap(_ => cycle("example7.json", "example7.raml", AmfJsonHint, Aml))
  }

  test("generate 8 test") {
    init().flatMap(_ => cycle("example8.json", "example8.raml", AmfJsonHint, Aml))
  }

  test("generate 9 test") {
    init().flatMap(_ => cycle("example9.json", "example9.raml", AmfJsonHint, Aml))
  }

  test("generate 10 test") {
    init().flatMap(_ => cycle("example10.json", "example10.raml", AmfJsonHint, Aml))
  }

  test("generate 11 test") {
    init().flatMap(_ => cycle("example11.json", "example11.raml", AmfJsonHint, Aml))
  }

  test("generate 12 test") {
    init().flatMap(_ => cycle("example12.json", "example12.raml", AmfJsonHint, Aml))
  }

  test("generate 13 test") {
    init().flatMap(_ => cycle("example13.json", "example13.raml", AmfJsonHint, Aml))
  }

  test("generate 14 test") {
    init().flatMap(_ => cycle("example14.json", "example14.raml", AmfJsonHint, Aml))
  }

  test("generate 15 test") {
    init().flatMap(_ => cycle("example15.json", "example15.raml", AmfJsonHint, Aml))
  }

  test("generate 16 test") {
    init().flatMap(_ => cycle("example16.json", "example16.raml", AmfJsonHint, Aml))
  }

  test("generate 17 test") {
    init().flatMap(_ => cycle("example17.json", "example17.raml", AmfJsonHint, Aml))
  }

  test("generate 18 test") {
    init().flatMap(_ => cycle("example18.json", "example18.raml", AmfJsonHint, Aml))
  }

  test("generate 19 test") {
    init().flatMap(_ => cycle("example19.json", "example19.raml", AmfJsonHint, Aml))
  }

  test("generate 20 test") {
    init().flatMap(_ => cycle("example20.json", "example20.raml", AmfJsonHint, Aml))
  }

  test("generate 21 test") {
    init().flatMap(_ => cycle("example21.json", "example21.raml", AmfJsonHint, Aml))
  }

  test("generate mappings_lib test") {
    init().flatMap(_ => cycle("mappings_lib.json", "mappings_lib.raml", AmfJsonHint, Aml))
  }

  // Key Property tests
  test("parse 19 test - with key property") {
    init().flatMap(_ => cycle("keyproperty/example19-keyproperty.raml", "keyproperty/example19-keyproperty.json", VocabularyYamlHint, Amf))
  }

  test("generate 19 test - with key property") {
    init().flatMap(_ => cycle("keyproperty/example19-keyproperty.json", "keyproperty/example19-keyproperty.raml", AmfJsonHint, Aml))
  }

}
