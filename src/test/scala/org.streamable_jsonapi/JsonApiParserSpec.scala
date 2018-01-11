package package com.github.lkalwa.scala_streamable_jsonapi

import java.io.ByteArrayInputStream

import org.scalatest._

class HandlerForTesting extends JsonApiHandler {
  var objectMap = Map[String, Any]()
  var timestamps = collection.mutable.MutableList[String]()
  var states = collection.mutable.MutableList[String]()
  var metaObject = Map[String, Any]()

  override def resource(obj: Map[String, Any]): Unit = {
    objectMap = obj
    timestamps.+=(System.nanoTime().toString)
  }

  override def startDocument(): Unit = states += "startDocument"

  override def endDocument(): Unit = states += "endDocument"

  override def startData: Unit = states += "startData"

  override def endData: Unit = states += "endData"

  override def data(obj: Map[String, Any]): Unit = {
    objectMap = obj
    states += "dataWithoutStreaming"
  }

  override def startIncluded: Unit = states += "startIncluded"

  override def endIncluded: Unit = states += "endIncluded"

  override def startErrors: Unit = states += "startErrors"

  override def endErrors: Unit = states += "endErrors"

  override def error(obj: Map[String, Any]): Unit = {}

  override def meta(obj: Map[String, Any]): Unit = {
    metaObject = obj
    states += "meta"
  }

  override def jsonapi(obj: Map[String, Any]): Unit = {}

  override def links(obj: Map[String, Any]): Unit = {}
}


class JsonApiParserSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  var handler: HandlerForTesting = _

  override def beforeEach(): Unit =  handler = new HandlerForTesting()

  def prepare(input: String): Unit = {
    new JsonApiParser(new ByteArrayInputStream(input.getBytes("UTF-8")), handler).readStream
  }

  "Parser" should "parse objects" in {
    prepare("""{"data" : {"type" : "user", "id" : "10", "relationships": [{"name" : "rel1"}, {"name" : "rel2"}] }}""")
    handler.objectMap should equal(Map("type" -> "user", "id" -> "10",
      "relationships" -> List(Map("name" -> "rel2"), Map("name" -> "rel1"))))
  }

  it should "parse arrays" in {
    prepare("""{"data" : {"type" : "user", "id" : "10", "relationships": [{"name" : "rel1"}, {"name" : "rel2"}] }}""")
    handler.objectMap should equal(
      Map("type" -> "user", "id" -> "10", "relationships" -> List(Map("name" -> "rel2"), Map("name" -> "rel1"))))
  }

  it should "not build maps from top level values" in  {
    prepare("""{"data" : {"type" : "user", "id" : "10", "relationships": [{"name" : "rel1"}, {"name" : "rel2"}] }}""")
    assert(!handler.objectMap.contains("data"))
  }

  it should "notify about beginning and end of section" in {
    prepare("""{"data" : [{}], "included": [{}]}""")
    handler.states should equal(List("startDocument","startData", "endData", "startIncluded", "endIncluded", "endDocument"))
  }

  it should "pass extracted resource attributes as soon as it's parser" in {
    prepare("""{"data" : [{"id": "1"}, {"id":"2"}, {"id":"3"}, {"id":"4"}]}""")
    assert(handler.timestamps.toSet.size == 4 )
  }

  it should "parse nested attributes array as any other simple attribute" in {
    prepare("""{"data" : [{"id": "1", "relationships" : [{"id" : "11"},{"id": "22"},{"id": "33"}]}, {"id" : "2"}]}""")
    assert(handler.timestamps.toSet.size == 2)
  }

  it should "recognize that data with single resource inside should not be streamed" in {
    prepare("""{"data" : {"id": "1", "relationships" : [{"id" : "11"},{"id": "22"},{"id": "33"}]}}""")
    handler.states should equal(List("startDocument", "dataWithoutStreaming", "endDocument"))
  }

  it should "handle non streamable members properly" in {
    prepare("""{"meta" : {"id": "1", "relationships" : [{"id" : "11"},{"id": "22"},{"id": "33"}]}}""")
    handler.states should equal(List("startDocument", "meta", "endDocument"))
  }
}