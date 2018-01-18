package com.github.lkalwa.scala_streamable_jsonapi

import org.codehaus.jackson.JsonFactory

class JsonApiGenerator(outputStream: java.io.OutputStream) extends JsonApiHandler {
  private val generator = new JsonFactory().createJsonGenerator(outputStream)
  private var currentSection = ""

  override def startDocument(): Unit = generator.writeStartObject()

  override def endDocument(): Unit = {
    generator.writeEndObject()
    generator.close()
  }

  override def startData(): Unit = {
    fieldName("data", true)
    startArray
  }

  override def endData(): Unit = endArray("data")

  override def data(obj: Map[String, Any]) = {
    fieldName("data", true)
    jsonObject(obj)
  }

  override def startIncluded(): Unit = {
    fieldName("included", true)
    startArray
  }

  override def endIncluded(): Unit = endArray("included")

  override def resource(obj: Map[String, Any]): Unit = jsonObject(obj)

  override def startErrors: Unit = {
    fieldName("errors", true)
    startArray()
  }

  override def endErrors: Unit = endArray("errors")

  override def error(obj: Map[String, Any]): Unit = jsonObject(obj)

  override def meta(obj: Map[String, Any]): Unit = {
    fieldName("meta", true)
    jsonObject(obj)
  }

  override def jsonapi(obj: Map[String, Any]): Unit = {
    fieldName("jsonapi", true)
    jsonObject(obj)
  }

  override def links(obj: Map[String, Any]): Unit = {
    fieldName("links", true)
    jsonObject(obj)
  }

  private def fieldName(str: String, topLevelSection: Boolean = false): Unit = {
    if (topLevelSection) currentSection = str
    generator.writeFieldName(str)
  }

  private def writeValue(value: Any): Unit =
    value match {
      case arr: List[Any] => writeArray(arr)
      case obj: Map[String, Any] => jsonObject(obj)
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

  private def startArray(): Unit = generator.writeStartArray()

  private def startObject: Unit = generator.writeStartObject()

  private def endObject: Unit = generator.writeEndObject()

  private def endArray(sectionName: String): Unit =
    if (currentSection == sectionName) generator.writeEndArray else throw new Exception(s"${currentSection} has no ending")
}
