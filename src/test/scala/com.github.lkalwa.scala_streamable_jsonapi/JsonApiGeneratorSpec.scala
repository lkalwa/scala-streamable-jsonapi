package com.github.lkalwa.scala_streamable_jsonapi

import java.io.ByteArrayOutputStream

import org.scalatest._
import play.api.libs.json._


class JsonApiGeneratorSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  var outputStream: ByteArrayOutputStream = _
  var generator: JsonApiGenerator = _

  override def beforeEach(): Unit =  {
    outputStream = new ByteArrayOutputStream()
    generator = new JsonApiGenerator(outputStream)
  }
  
  def startDoc(): Unit = generator.startDocument()

  def startData(): Unit = generator.startData()

  def endData(): Unit = generator.endData()

  def data(map: Map[String, Any]) = generator.data(map)

  def endDoc(): Unit = generator.endDocument()

  def resource(map: Map[String, Any]): Unit = generator.resource(map)

  def close(): Unit = generator.close()

  def startAtomicResults() = generator.startAtomicResults()

  def atomicResult(obj: Map[String, Any]) = generator.atomicResult(obj)

  def endAtomicResults() = generator.endAtomicResults()

  def matchContent(str: String) = outputStream.toString should equal(str)

  "Generator" should "generate objects" in {
    startDoc()
    data(Map("type" -> "role",
      "attributes" -> Map("title_name" -> "CEO", "name" -> "CEO", "role_salary" -> 5000000)))
    endDoc()
    close()
    matchContent(
      """{"data" : {"type" : "role",
        | "attributes" : {"title_name" : "CEO", "name" : "CEO", "role_salary" : 5000000}}}""".stripMargin.replaceAll("\\s", ""))
  }

  it should "generate arrays" in {
    startDoc()
    startData()
    resource(Map("type" -> "role", "attributes" -> Map("title_name" -> "CEO", "name" -> "CEO", "role_salary" -> 5000000)))
    resource(Map("type" -> "role", "attributes" -> Map("title_name" -> "CFO", "name" -> "CFO", "role_salary" -> 4000000)))
    endData()
    endDoc()
    close()
    matchContent(
      """{"data" : [{"type" : "role", "attributes" : {"title_name" : "CEO", "name" : "CEO", "role_salary" : 5000000}},
        | {"type" : "role", "attributes" : {"title_name" : "CFO", "name" : "CFO", "role_salary" : 4000000} }]}""".stripMargin.replaceAll("\\s", ""))
  }

  it should "work properly with non streamable sections" in {
    startDoc()
    generator.links(Map("self" -> "http://example.com/posts"))
    endDoc()
    close()
    matchContent(
      """{"links" : {"self" : "http://example.com/posts"}}""".stripMargin.replaceAll("\\s", ""))
  }

  it should "raise Exception if client is using methods not correct in terms of current location" in {
    startDoc()
    startData()
    resource(Map("type" -> "role", "attributes" -> Map("title_name" -> "CEO", "name" -> "CEO", "role_salary" -> 5000000)))
    close()
    val exception = intercept[java.lang.Exception] { generator.endErrors() }
    exception.getMessage should equal("data has no ending")
  }

  it should "handle numeric values" in {
    startDoc()
    data(Map("value" -> 10.5))
    endDoc()
    close()
    Json.parse(outputStream.toString).\("data").\("value").get should equal(JsNumber(10.5))
  }

  it should "handle integer values" in {
    startDoc()
    data(Map("value" -> 10))
    endDoc()
    close()
    Json.parse(outputStream.toString).\("data").\("value").get should equal(JsNumber(10))
  }

  it should "handle boolean values" in {
    startDoc()
    data(Map("value" -> true))
    endDoc()
    close()
    Json.parse(outputStream.toString).\("data").\("value").get should equal(JsTrue)
  }

  it should "handle null values" in {
    startDoc()
    data(Map("value" -> null))
    endDoc()
    close()
    Json.parse(outputStream.toString).\("data").\("value").get should equal(JsNull)
  }

  it should "properly return operation" in {
    startDoc()
    startAtomicResults()
    atomicResult(
      Map("op" -> "add",
      "data" -> Map("type" -> "structures", "id" -> "12",
        "attributes" -> Map("name" -> "StructureName"),
        "relationships" -> Map("phase" -> Map("data" -> Map("type" -> "phases", "id" -> "123"))))
      )
    )
    endAtomicResults()
    endDoc()
    close()
    matchContent(
      """
        |{
        |  "atomic:results":[{
        |    "op": "add",
        |    "data":{
        |      "type":"structures",
        |      "id": "12",
        |      "attributes": {"name": "StructureName"},
        |      "relationships": {"phase": {"data": {"type": "phases","id": "123"}}}
        |    }
        |  }]
        |}  """.stripMargin.replaceAll("\\s", "")
    )
  }
}