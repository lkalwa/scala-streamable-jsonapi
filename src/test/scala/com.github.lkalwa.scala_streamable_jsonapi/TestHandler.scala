package com.github.lkalwa.scala_streamable_jsonapi

class TestHandler extends JsonApiHandler {
  var objectMap = Map[String, Any]()
  var timestamps = collection.mutable.MutableList[String]()
  var states = collection.mutable.MutableList[String]()
  var errors = collection.mutable.MutableList[Map[String, Any]]()
  var metaObject = Map[String, Any]()
  var links = Map[String, Any]()
  var jsonapi = Map[String, Any]()

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

  override def startOperations(): Unit =
    states += "startOperations"

  override def endOperations(): Unit = states += "endOperations"

  override def error(obj: Map[String, Any]): Unit = {
    errors += obj
  }

  override def meta(obj: Map[String, Any]): Unit = {
    metaObject = obj
    states += "meta"
  }

  override def jsonapi(obj: Map[String, Any]): Unit = jsonapi = obj

  override def links(obj: Map[String, Any]): Unit = links = obj

  override def add(obj: Map[String, Any]): Unit = objectMap = obj
}