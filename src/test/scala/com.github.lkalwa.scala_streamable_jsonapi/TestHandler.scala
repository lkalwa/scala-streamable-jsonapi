package com.github.lkalwa.scala_streamable_jsonapi

import scala.collection.mutable

class TestHandler extends JsonApiHandler {
  var timestamps = mutable.MutableList[String]()
  var states: mutable.MutableList[String] = collection.mutable.MutableList[String]()
  var errors = mutable.MutableList[Map[String, Any]]()
  var metaObject: Map[String, Any] = Map[String, Any]()
  var links: Map[String, Any] = Map[String, Any]()
  var jsonapi: Map[String, Any] = Map[String, Any]()
  var operationType: String = _

  override def resource(obj: JsonApiResource): Unit = {
    timestamps.+=(System.nanoTime().toString)
  }

  override def startDocument(): Unit = states += "startDocument"

  override def endDocument(): Unit = states += "endDocument"

  override def startData(): Unit = states += "startData"

  override def endData(): Unit = states += "endData"

  override def data(obj: JsonApiResource): Unit = {
    states += "dataWithoutStreaming"
  }

  override def startIncluded(): Unit = states += "startIncluded"

  override def endIncluded(): Unit = states += "endIncluded"

  override def startErrors(): Unit = states += "startErrors"

  override def endErrors(): Unit = states += "endErrors"

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

  override def operation(opType: String, obj: JsonApiResource): Unit = {
    operationType = opType
    super.operation(opType, obj)

  }
}