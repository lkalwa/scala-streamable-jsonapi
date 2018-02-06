package com.github.lkalwa.scala_streamable_jsonapi

trait JsonApiHandler {

  def startDocument: Any = {}

  def endDocument: Any = {}

  def startData: Any = {}

  def endData: Any = {}

  def data(obj: Map[String, Any]): Any = {}

  def startIncluded: Any = {}

  def endIncluded: Any = {}

  def resource(obj: Map[String, Any]): Any = {}

  def startErrors: Any = {}

  def endErrors: Any = {}

  def error(obj: Map[String, Any]): Any = {}

  def meta(obj: Map[String, Any]): Any = {}

  def jsonapi(obj: Map[String, Any]): Any = {}

  def links(obj: Map[String, Any]): Any = {}
}
