package com.github.lkalwa.scala_streamable_jsonapi

import java.util
import collection.JavaConverters._

class JsonApiResource(map: Map[String, Any], handler: JsonApiHandler) extends TypedValue {
  protected val scalaMap: Map[String, AnyRef] = map.asInstanceOf[Map[String, AnyRef]]

  val resTypeOption: Option[String] = typedStringGet(scalaMap, "type", identity)
  val localIdOption: Option[String] = typedStringGet(scalaMap, "local:id", identity)
  val idOption: Option[String] = typedStringGet(scalaMap, "id", identity)
  private val hasAnyId: Boolean =
    List(localIdOption, idOption).map(value => value.isDefined && value.exists(_.nonEmpty)).exists(identity)
  private val hasType: Boolean = resTypeOption.exists(_.nonEmpty)
  val isValid: Boolean = hasAnyId && hasType

  runValidityCheck()

  def runValidityCheck(): Unit = {
    if (!hasAnyId) handler.error(Map("details" -> "missing id or local:id"))
    if (!hasType) handler.error(Map("details" -> "missing type"))
  }

  val attributes: Map[String, AnyRef] =
    typedGet[Map[String, AnyRef], Map[String, AnyRef]](scalaMap, "attributes", identity).getOrElse(Map())

  val attributesAsJava: util.Map[String, AnyRef] = attributes.asJava

  private val rawRelationships =
    typedGet[Map[String, AnyRef], Map[String, Map[String, Iterable[AnyRef]]]](scalaMap, "relationships",
      _.mapValues(_.asInstanceOf[Map[String, Map[String, Iterable[AnyRef]]]]))
      .getOrElse(Map())

  val relationships: Map[String, Option[Iterable[JsonApiResource]]] =
    rawRelationships.map { case (name, relDetails) =>
      name -> initializeRels(relDetails.getOrElse("data", Map())) }

  val relationshipsAsJava: util.Map[String, Iterable[JsonApiResource]] =
    relationships.map { case (name, rels) => name -> rels.orNull }.asJava

  private def asCollection: Iterable[AnyRef] => List[Map[String, String]] =
    {
      case iterableWithMap: List[Map[String, String]] => iterableWithMap
      case singleMap => List(singleMap.asInstanceOf[Map[String, String]])
    }

  private def initializeRels(relationshipsDescription: AnyRef): Option[List[JsonApiResource]] =
    relationshipsDescription match {
      case null => Option.empty
      case iterable: Iterable[AnyRef] => Option(asCollection(iterable).map(rel => new JsonApiResource(rel, handler)))
    }
}
