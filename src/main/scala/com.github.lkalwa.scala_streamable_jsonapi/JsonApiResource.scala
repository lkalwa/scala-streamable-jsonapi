package com.github.lkalwa.scala_streamable_jsonapi

import scala.collection.MapLike


class JsonApiResource(map: Map[String, Any], handler: JsonApiHandler) extends BasicJsonApiResource(map, handler) {

  val attributes: Map[String, AnyRef] =
    typedGet[Map[String, AnyRef], Map[String, AnyRef]](scalaMap, "attributes", identity).getOrElse(Map())


  //"employee":{"data":{"type":"employees","id":"10"}}
  //"relationships":{"locations":{"data":[{"type":"locations","id":"1"}, {"type":"locations","id":"2"}]}}}}

  private val rawRelationships =
    typedGet[Map[String, AnyRef], Map[String, Map[String, Iterable[AnyRef]]]](scalaMap, "relationships",
      _.mapValues(_.asInstanceOf[Map[String, Map[String, Iterable[AnyRef]]]]))
      .getOrElse(Map())

  val relationships: Map[String, Option[Iterable[BasicJsonApiResource]]] =
    rawRelationships.collect { case (name, relDetails) =>
      name -> initializeRels(relDetails.getOrElse("data", Map())) }

  private def asCollection: Iterable[AnyRef] => List[Map[String, String]] =
    {
      case iterableWithMap: List[Map[String, String]] => iterableWithMap
      case _ => List(map.asInstanceOf[Map[String, String]])
    }

  private def initializeRels(relationshipsDescription: AnyRef): Option[List[BasicJsonApiResource]] =
    Option(relationshipsDescription match {
      case null => None
      case iterable: Iterable[AnyRef] =>
        asCollection(iterable).map(rel => new BasicJsonApiResource(rel, handler))
    }).asInstanceOf[Option[List[BasicJsonApiResource]]]
}
