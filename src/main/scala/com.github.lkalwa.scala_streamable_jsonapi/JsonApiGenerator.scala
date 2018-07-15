package com.github.lkalwa.scala_streamable_jsonapi

import org.codehaus.jackson.JsonFactory

class JsonApiGenerator(outputStream: java.io.OutputStream) {
  private val generator = new JsonFactory().createJsonGenerator(outputStream)
  private var currentSection = ""

  def startDocument(): Unit = generator.writeStartObject()

  def endDocument(): Unit = {
    generator.writeEndObject()
    generator.close()
  }

   def startData: Unit = {
    fieldName("data", true)
    startArray
  }

   def endData: Unit = endArray("data")

   def data(obj: Map[String, Any]): Unit = {
    fieldName("data", true)
    jsonObject(obj)
  }

   def startIncluded: Unit = {
    fieldName("included", true)
    startArray
  }

   def endIncluded: Unit = endArray("included")

   def resource(obj: Map[String, Any]): Unit = jsonObject(obj)

   def startErrors: Unit = {
    fieldName("errors", true)
    startArray
  }

   def endErrors: Unit = endArray("errors")

   def error(obj: Map[String, Any]): Unit = jsonObject(obj)

   def meta(obj: Map[String, Any]): Unit = {
    fieldName("meta", true)
    jsonObject(obj)
  }

   def jsonapi(obj: Map[String, Any]): Unit = {
    fieldName("jsonapi", true)
    jsonObject(obj)
  }

   def links(obj: Map[String, Any]): Unit = {
    fieldName("links", true)
    jsonObject(obj)
  }

  private def fieldName(str: String, topLevelSection: Boolean = false): Unit = {
    if (topLevelSection) currentSection = str
    generator.writeFieldName(str)
  }

  private def writeValue(value: Any): Unit =
    value match {
      case arr: List[_] => writeArray(arr)
      case map: Map[_, _] => jsonObject(map.asInstanceOf[Map[String, Any]])
      case _ => generator.writeString(value.toString)
    }

  private def writeArray(arr: List[Any]): Unit = arr.foreach(obj => jsonObject(obj.asInstanceOf[Map[String, Any]]))

  private def jsonObject(map: Map[String, Any]): Unit = {
    startObject
    map.foreach { case (k, v) =>
      fieldName(k)
      writeValue(v)
    }
    endObject
  }

  private def startArray(): Unit = generator.writeStartArray

  private def startObject: Unit = generator.writeStartObject

  private def endObject: Unit = generator.writeEndObject

  private def endArray(sectionName: String): Unit =
    if (currentSection == sectionName) generator.writeEndArray else throw new Exception(s"${currentSection} has no ending")
}
