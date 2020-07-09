package com.github.lkalwa.scala_streamable_jsonapi

import java.io.ByteArrayInputStream

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.collection.mutable

class JsonApiParserSpec extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach {
  var handler: TestHandler = _
  var parser: TestParser[TestHandler] = _
  var parsedMap: Map[String, Any] = Map[String, Any]()

  class TestParser[H <: JsonApiHandler](inputStream: java.io.InputStream, handler: H)
    extends JsonApiParser(inputStream, handler) {
    override def passObject(map: Map[String, Any]): Map[String, Any] = {
      parsedMap = map
      super.passObject(map)
    }
  }

  override def beforeEach(): Unit = handler = new TestHandler()

  def prepare(input: String): Unit =
    new TestParser(new ByteArrayInputStream(input.getBytes("UTF-8")), handler).readStream

  "Parser" should "parse objects" in {
    prepare("""{"data":{"type":"users","id":"10"}}""")
    parsedMap should equal(Map("type"->"users","id"->"10"))
  }

  it should "parse arrays" in {
    prepare("""{"data":{"type":"user","id":"10","attributes":{"grades":["A","B","C","D"]}}}""")
    parsedMap should equal(
      Map("type"->"user","id"->"10","attributes"-> Map("grades"-> List("D","C","B","A")))
    )
  }

  it should "not build maps from top level values" in {
    prepare("""{"data":{"type":"user","id":"10"}}""")
    assert(!parsedMap.contains("data"))
  }

  it should "notify about beginning and end of section" in {
    prepare("""{"data":[{}],"included":[{}]}""")
    handler.states should equal(mutable.MutableList("startDocument", "startData", "endData", "startIncluded", "endIncluded", "endDocument"))
  }

  it should "pass extracted resource attributes as soon as they're parsed" in {
    prepare("""{"data":[{"id":"1"},{"id":"2"},{"id":"3"},{"id":"4"}]}""")
    assert(handler.timestamps.toSet.size == 4)
  }


  it should "recognize that data with single resource inside should not be streamed" in {
    prepare("""{"data" : {"id": "1","type":"users"}}""")
    handler.states should equal(List("startDocument", "dataWithoutStreaming", "endDocument"))
  }

  it should "handle non streamable members properly" in {
    prepare("""{"meta" : {"id": "1", "relationships" : [{"id" : "11"},{"id": "22"},{"id": "33"}]}}""")
    handler.states should equal(List("startDocument", "meta", "endDocument"))
  }

  it should "extract errors from streamed 'errors' section" in {
    prepare("""{"errors": [{"id": "1", "status": "403", "title": "forbidden"}, {"id": "2", "status": "404", "title": "not found"} ] }""")
    handler.errors should equal(mutable.MutableList(Map("id" -> "1", "status" -> "403", "title" -> "forbidden"),
      Map("id" -> "2", "status" -> "404", "title" -> "not found")))
  }

  it should "work with jsonapi object" in {
    prepare("""{"jsonapi": {"version": "1.1"}}""")
    handler.jsonapi should equal(Map("version" -> "1.1"))
  }

  it should "work with links object" in {
    prepare(
      """{"links": {"self":
        |"https://github.com/lkalwa/scala-streamable-jsonapi/blob/master/src/test/scala/com.github.lkalwa.scala_streamable_jsonapi/JsonApiParserSpec.scala"}}""".stripMargin)
    handler.links should equal(Map("self" ->
      "https://github.com/lkalwa/scala-streamable-jsonapi/blob/master/src/test/scala/com.github.lkalwa.scala_streamable_jsonapi/JsonApiParserSpec.scala"))
  }

  behavior of "Parser when dealing with operation extension"

  it should "extract operation from parsed document and call corresponding method" in {
    prepare(
      """
        |{
        |  "atomic:operations":[{
        |    "op": "add",
        |    "data":{
        |      "type": "structures",
        |      "local:id": "12",
        |      "attributes": {"name": "Structure Name"},
        |      "relationships": {"phase": {"data": {"type": "phases","id": "123"}}}
        |    }
        |  }]
        |}""".stripMargin
    )
    handler.operationType should equal("add")
  }
}

