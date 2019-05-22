package com.github.lkalwa.scala_streamable_jsonapi

import com.fasterxml.jackson.core.JsonToken
import scala.collection.mutable.ArrayStack

class JsonApiStack extends ArrayStack[JsonToken] {

  def topLevelMember: Boolean = equal(JsonToken.START_OBJECT)

  def topLevelMemberArray: Boolean = equal(JsonToken.START_ARRAY, JsonToken.START_OBJECT)

  def inTopLevelMemberArray: Boolean = equal(JsonToken.START_OBJECT, JsonToken.START_ARRAY, JsonToken.START_OBJECT)

  def singleTopLevelObject: Boolean = equal(JsonToken.START_OBJECT, JsonToken.START_OBJECT)

  private def equal(elements: JsonToken*): Boolean = toSeq == elements
}
