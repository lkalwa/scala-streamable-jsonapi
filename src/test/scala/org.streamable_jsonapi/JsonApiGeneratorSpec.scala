package org.streamable_jsonapi

import java.io.ByteArrayOutputStream

import org.scalatest._

class JsonApiGeneratorSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  var outputStream: ByteArrayOutputStream = _
  var generator: JsonApiGenerator = _

  override def beforeEach(): Unit =  {
    outputStream = new ByteArrayOutputStream()
    generator = new JsonApiGenerator(outputStream)
  }

  override def afterEach(): Unit = outputStream.close()

  "Generator" should "generate objects" in {
    generator.startDocument
    generator.data(Map("type" -> "role",
      "attributes" -> Map("title_name" -> "CEO", "name" -> "CEO", "role_salary" -> 5000000)))
    generator.endDocument
    outputStream.toString should equal(
      """{"data" : {"type" : "role",
        | "attributes" : {"title_name" : "CEO", "name" : "CEO", "role_salary" : "5000000"}}}""".stripMargin.replaceAll("\\s", ""))
  }

  it should "generate arrays" in {
    generator.startDocument
    generator.startData
    generator.resource(Map("type" -> "role", "attributes" -> Map("title_name" -> "CEO", "name" -> "CEO", "role_salary" -> 5000000)))
    generator.resource(Map("type" -> "role", "attributes" -> Map("title_name" -> "CFO", "name" -> "CFO", "role_salary" -> 4000000)))
    generator.endData
    generator.endDocument
    outputStream.toString should equal(
      """{"data" : [{"type" : "role", "attributes" : {"title_name" : "CEO", "name" : "CEO", "role_salary" : "5000000"}},
        | {"type" : "role", "attributes" : {"title_name" : "CFO", "name" : "CFO", "role_salary" : "4000000"} }]}""".stripMargin.replaceAll("\\s", ""))
  }

  it should "work properly with non streamable sections" in {
    generator.startDocument
    generator.links(Map("self" -> "http://example.com/posts"))
    generator.endDocument
    outputStream.toString should equal(
      """{"links" : {"self" : "http://example.com/posts"}}""".stripMargin.replaceAll("\\s", ""))
  }

  it should "raise Exception if client is using bad methods" in {
    generator.startDocument
    generator.startData
    generator.resource(Map("type" -> "role", "attributes" -> Map("title_name" -> "CEO", "name" -> "CEO", "role_salary" -> 5000000)))
    val exception = intercept[java.lang.Exception] { generator.endErrors }
    exception.getMessage should equal("data has no ending")
  }
}