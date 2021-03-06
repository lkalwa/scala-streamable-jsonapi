package com.github.lkalwa.scala_streamable_jsonapi

import com.fasterxml.jackson.core.JsonFactory

import scala.collection.mutable

class JsonApiGenerator(outputStream: java.io.OutputStream) {
  private val generator = new JsonFactory().createGenerator(outputStream)
  private var currentSection = ""

  def startDocument(): Unit = generator.writeStartObject()

  def endDocument(): Unit = generator.writeEndObject()

  def startData(): Unit = {
    fieldName("data", topLevelSection = true)
    startArray()
  }

  def endData(): Unit = endArray("data")

  def data(obj: Map[String, Any]): Unit = {
    fieldName("data", topLevelSection = true)
    jsonObject(obj)
  }

  def data(obj: collection.mutable.Map[String, Any]): Unit = jsonObject(obj.toMap)

  def startIncluded(): Unit = {
    fieldName("included", topLevelSection = true)
    startArray()
  }

  def endIncluded(): Unit = endArray("included")

  def resource(obj: Map[String, Any]): Unit = jsonObject(obj)
  def resource(obj: mutable.Map[String, Any]): Unit = jsonObject(obj.toMap)

  def startErrors(): Unit = {
    fieldName("errors", topLevelSection = true)
    startArray()
  }

  def endErrors(): Unit = endArray("errors")

  def error(obj: Map[String, Any]): Unit = jsonObject(obj)
  def error(obj: mutable.Map[String, Any]): Unit = jsonObject(obj.toMap)

  def meta(obj: Map[String, Any]): Unit = {
    fieldName("meta", topLevelSection = true)
    jsonObject(obj)
  }

  def jsonapi(obj: Map[String, Any]): Unit = {
    fieldName("jsonapi", topLevelSection = true)
    jsonObject(obj)
  }

  def links(obj: Map[String, Any]): Unit = {
    fieldName("links", topLevelSection = true)
    jsonObject(obj)
  }

  def startAtomicResults(): Unit = {
    fieldName("atomic:results", topLevelSection = true)
    startArray()
  }

  def atomicResult(obj: Map[String, Any]): Unit = jsonObject(Map("data" -> obj))
  def atomicResult(obj: mutable.Map[String, Any]): Unit = jsonObject(Map("data" -> obj))

  def endAtomicResults(): Unit = endArray("atomic:results")

  private def fieldName(str: String, topLevelSection: Boolean = false): Unit = {
    if (topLevelSection) currentSection = str
    generator.writeFieldName(str)
  }

  private def writeValue(value: Any): Unit =
    value match {
      case arr: List[_] => writeArray(arr)
      case map: Map[_, _] => jsonObject(map.asInstanceOf[Map[String, Any]])
      case num: java.lang.Number => generator.writeNumber(num.toString)
      case bool: Boolean => generator.writeBoolean(bool)
      case null => generator.writeNull()
      case _ => generator.writeString(value.toString)
    }

  private def writeArray(arr: List[Any]): Unit = {
    startArray()
    arr.foreach(obj => jsonObject(obj.asInstanceOf[Map[String, Any]]))
    endArray()
  }

  private def jsonObject(map: Map[String, Any]): Unit = {
    startObject()
    map.foreach { case (k, v) =>
      fieldName(k)
      writeValue(v)
    }
    endObject()
  }

  private def startArray(): Unit = generator.writeStartArray()

  private def startObject(): Unit = generator.writeStartObject()

  private def endObject(): Unit = generator.writeEndObject()

  private def endArray(sectionName: String): Unit =
    if (currentSection == sectionName) endArray() else throw new Exception(s"${currentSection} has no ending")

  private def endArray(): Unit = generator.writeEndArray()

  def close(): Unit = generator.close()
}
