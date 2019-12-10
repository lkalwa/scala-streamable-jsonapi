package com.github.lkalwa.scala_streamable_jsonapi

trait JsonApiHandler {

  def startDocument(): Unit = {}

  def endDocument(): Unit = {}

  def startData(): Unit = {}

  def endData(): Unit = {}

  def data(obj: Map[String, Any]): Unit = {}

  def startIncluded(): Unit = {}

  def endIncluded(): Unit = {}

  def resource(obj: Map[String, Any]): Unit = {}

  def startErrors(): Unit = {}

  def endErrors(): Unit = {}

  def error(obj: Map[String, Any]): Unit = {}

  def meta(obj: Map[String, Any]): Unit = {}

  def jsonapi(obj: Map[String, Any]): Unit = {}

  def links(obj: Map[String, Any]): Unit = {}

  def startOperations(): Unit = {}

  def endOperations(): Unit = {}

  def operation(opType: String, obj: Map[String, Any]): Unit =
    opType match {
      case "add" => add(obj)
      case "update" => update(obj)
      case "remove" => remove(obj)
      case _ => None
    }

  def add(obj: Map[String, Any]): Unit = {}

  def remove(obj: Map[String, Any]): Unit = {}

  def update(obj: Map[String, Any]): Unit = {}
}
