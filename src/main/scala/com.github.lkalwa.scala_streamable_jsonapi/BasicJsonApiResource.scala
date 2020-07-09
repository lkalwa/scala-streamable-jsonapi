package com.github.lkalwa.scala_streamable_jsonapi

import scala.collection.mutable.ArrayBuffer

class BasicJsonApiResource(map: Map[String, Any], handler: JsonApiHandler) extends TypedValue {
  protected val scalaMap = map.asInstanceOf[Map[String, AnyRef]]
  val errors = ArrayBuffer[String]()

  val resTypeOption: Option[String] = typedStringGet(scalaMap, "type", identity)
  val localIdOption = typedStringGet(scalaMap, "local:id", identity)
  val idOption = typedStringGet(scalaMap, "id", identity)
  private val hasAnyId: Boolean = List(localIdOption, idOption).map(_.isDefined).exists(identity)
  private val hasType: Boolean = resTypeOption.exists(!_.isEmpty)
  val isValid: Boolean = hasAnyId && hasType

  runValidityCheck()

  def runValidityCheck(): Unit = {
    if (!hasAnyId) handler.error(Map("details" -> "missing id or local:id"))
    if (!hasType) handler.error(Map("details" -> "missing type"))
  }
}
