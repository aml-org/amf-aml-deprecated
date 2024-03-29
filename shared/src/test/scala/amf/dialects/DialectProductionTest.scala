package amf.dialects

import amf.core.remote._
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext


class DialectProductionTest extends DialectTests with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = init()

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override val basePath = "shared/src/test/resources/vocabularies2/production/"

  ignore("Can parse the canonical webapi dialect") {
    cycle("canonical_webapi.yaml", "canonical_webapi.json", VocabularyYamlHint, Amf, "vocabularies/dialects/")
  }

  test("Can parse validation dialect eee") {
    cycle("validation_dialect.raml", "validation_dialect.json", VocabularyYamlHint, Amf)
  }

  test("Can parse validation dialect instance") {
    withDialect("validation_dialect.raml",
                "validation_instance1.raml",
                "validation_instance1.raml.raml",
                VocabularyYamlHint,
                Aml)
  }

  test("Can parse validation dialect cfg1 instance") {
    withDialect("example1.raml",
                "example1_instance.raml",
                "example1_instance.jsonld",
                VocabularyYamlHint,
                Amf,
                basePath + "cfg/")
  }

  test("Can parse validation dialect cfg2 instance") {
    withDialect("example2.raml",
                "example2_instance.raml",
                "example2_instance.jsonld",
                VocabularyYamlHint,
                Amf,
                basePath + "cfg/")
  }

  test("Can parse validation dialect cfg3 instance") {
    withDialect("example3.raml",
                "example3_instance.raml",
                "example3_instance.jsonld",
                VocabularyYamlHint,
                Amf,
                basePath + "cfg/")
  }


  test("Can parse ABOUT dialect") {
    cycle("ABOUT-dialect.raml", "ABOUT-dialect.jsonld", VocabularyYamlHint, Amf, basePath + "ABOUT/")
  }

  test("Can parse and generated ABOUT dialect") {
    cycle("ABOUT-dialect.raml", "ABOUT-dialect.raml.raml", VocabularyYamlHint, Aml, basePath + "ABOUT/")
  }

  test("Can parse and generate ABOUT dialect instance") {
    withDialect("ABOUT-dialect.raml", "ABOUT.yaml", "ABOUT.yaml.raml", VocabularyYamlHint, Aml, basePath + "ABOUT/")
  }

  test("Can parse and generate ABOUT-github dialect instance") {
    withDialect("ABOUT-GitHub-dialect.raml",
                "example.yaml",
                "example.yaml.raml",
                VocabularyYamlHint,
                Aml,
                basePath + "ABOUT/github/")
  }

  test("Can parse ABOUT-hosted dialectinstance") {
    withDialect("ABOUT-hosted-vcs-dialect.yaml",
                "ABOUT_hosted.yaml",
                "ABOUT_hosted.jsonld",
                VocabularyYamlHint,
                Amf,
                basePath + "ABOUT/")
  }

  test("Can parse and generate the Instagram dialect") {
    cycle("dialect.raml", "dialect.json", VocabularyYamlHint, Amf, basePath + "Instagram/")
  }

  test("Can parse and generate Instance dialect instance 1") {
    withDialect("dialect.raml", "instance1.raml", "instance1.json", VocabularyYamlHint, Amf, basePath + "Instagram/")
  }

  test("Can parse and generate Instance dialect instance 2") {
    withDialect("dialect.raml", "instance2.raml", "instance2.json", VocabularyYamlHint, Amf, basePath + "Instagram/")
  }

  test("Can parse and generate the activity dialect") {
    cycle("activity.yaml", "activity.json", VocabularyYamlHint, Amf, basePath + "streams/")
  }

  test("Can parse activity instances") {
    withDialect("activity.yaml", "stream1.yaml", "stream1.json", VocabularyYamlHint, Amf, basePath + "streams/")
  }

  test("Can parse activity deployments demo") {
    withDialect("dialect.yaml", "deployment.yaml", "deployment.json", VocabularyYamlHint, Amf, basePath + "deployments_demo/")
  }

  test("Can parse guids example") {
    withDialect("guids_dialect.yaml", "guids_instance.yaml", "guids_instance.json", VocabularyYamlHint, Amf, basePath + "guids/")
  }

}

class DialectProductionResolutionTest extends DialectInstanceResolutionCycleTests {


  override val basePath = "shared/src/test/resources/vocabularies2/production/"

  // Order is not predictable
  ignore("Can parse asyncapi overlay instances") {
    withDialect("dialect6.yaml",
                "patch6.yaml",
                "patch6.resolved.yaml",
                VocabularyYamlHint,
                Aml,
                basePath + "asyncapi/")
  }
}
