package com.github.lkalwa.scala_streamable_jsonapi

import com.fasterxml.jackson.core.{JsonFactory, JsonToken}

import scala.annotation.tailrec

class JsonApiParser[H <: JsonApiHandler](inputStream: java.io.InputStream, val handler: H) {
  private val parser = new JsonFactory().createParser(inputStream)
  private lazy val stack = new JsonApiStack()
  private var currentSection = ""
  private val streamedSections = List("data", "included", "errors")

  /**
   * According to jsonapi spec (http://jsonapi.org/format/):
   * `data` - may be an object OR array of objects, so it will be streamed if it's an array of objects
   * `errors`, `included` - have to be an array, and will be streamed
   * `jsonapi`, `meta`, `links` - are objects, thus no streaming.
   * If there is only one object for `data`, no `startData` and `endData` events will be called on handler,
   * parsed data object will be passed to `data` method on handler
   */

  @tailrec
  final def readStream: Unit = {
    if (parser.nextValue != null) {
      startDocument
      if (parser.getCurrentName != null) newSection
      val parsedData = parse
      if (!streamed && parsedData.isInstanceOf[Map[_, _]]) {
        passNonStreamedSectionToHandler(parsedData.asInstanceOf[Map[String, Any]])
      }
      readStream
    } else {
      endDocument
      parser.close
    }
  }

  private def parse: Any =
    parser.getCurrentToken match {
      case JsonToken.START_OBJECT => startObject
      case JsonToken.START_ARRAY => startArray
      case _ => parserValue
    }

  private def startObject: Any = {
    stack.push(JsonToken.START_OBJECT)
    parseObject()
  }

  @tailrec
  private def parseObject(map: Map[String, Any] = Map.empty): Map[String, Any] = {
    if (stack.topLevelMember) {
      Map.empty
    } else {
      parser.nextValue()
      if (completelyParsed && streamed) {
        passObject(map)
      } else if (endOfObject) {
        stack.pop
        map
      } else
        parseObject(map.updated(parser.getCurrentName, parse))
    }
  }

  private def endOfObject: Boolean = parser.getCurrentToken == JsonToken.END_OBJECT

  private def completelyParsed: Boolean = endOfObject && (stack.inTopLevelMemberArray || stack.singleTopLevelObject)

  private def passObject(map: Map[String, Any]): Map[String, Any] = {
    currentSection match {
      case "errors" => handler.error(map)
      case "data" if stack.singleTopLevelObject => handler.data(map) //special treatment for data which is not an Array
      case _ => handler.resource(map)
    }
    stack.pop
    Map.empty
  }

  private def startArray: Any = {
    stack.push(JsonToken.START_ARRAY)
    if (streamed && stack.topLevelMemberArray) {
      notifyHandlerAboutStreaming
      parseObjectsInArray
    } else
      parseWholeArray()
  }

  @tailrec
  private def parseObjectsInArray: Any = {
    parser.nextValue()
    if (endOfArray) {
      notifyIfEndOfSection
      stack.pop
    }  else {
      parse
      parseObjectsInArray
    }
  }

  @tailrec
  private def parseWholeArray(arr: List[Any] = List.empty): List[Any] = {
    parser.nextValue()
    if (endOfArray) {
      stack.pop
      arr
    } else
      parseWholeArray(arr.+:(parse))
  }

  private def endOfArray: Boolean = parser.getCurrentToken == JsonToken.END_ARRAY

  private def parserValue: Any =
    parser.getCurrentToken match {
      case JsonToken.VALUE_NUMBER_FLOAT | JsonToken.VALUE_NUMBER_INT => toScalaType(parser.getNumberValue)
      case JsonToken.VALUE_FALSE => false
      case JsonToken.VALUE_TRUE => true
      case JsonToken.VALUE_NULL => null
      case _ => parser.getText
    }

  private def newSection: Unit = if (!currentSection.isEmpty) currentSection = parser.getCurrentName

  private def notifyIfEndOfSection: Unit = if (streamed && parser.getCurrentName == currentSection) endStramingSection

  def startDocument: Unit =
    if (currentSection.isEmpty) {
      handler.startDocument
      currentSection = "document"
    }

  def endDocument: Unit = handler.endDocument

  def streamed: Boolean = currentSection == "document" || streamedSections.contains(currentSection)

  def notifyHandlerAboutStreaming: Unit =
    currentSection match {
      case "data" => handler.startData
      case "included" => handler.startIncluded
      case "errors" => handler.startErrors
      case _ => None
    }

  def endStramingSection: Unit =
    currentSection match {
      case "data" => handler.endData
      case "included" => handler.endIncluded
      case "error" => handler.endErrors
      case _ => None
    }

  def passNonStreamedSectionToHandler(map: Map[String, Any]): Unit =
    currentSection match {
      case "jsonapi" => handler.jsonapi(map)
      case "meta" => handler.meta(map)
      case "links" => handler.links(map)
      case _ => None
    }

  private def toScalaType(numericVal: java.lang.Number): Any = {
    def stringify: String = numericVal.toString

    numericVal match {
      case _: java.lang.Integer => stringify.toInt
      case _: java.lang.Long => stringify.toLong
      case _: java.lang.Float => stringify.toFloat
      case _: java.lang.Double => stringify.toDouble
      case _: java.lang.Short => stringify.toShort
      case _: java.lang.Byte => stringify.toByte
      case _ => numericVal
    }
  }
}
