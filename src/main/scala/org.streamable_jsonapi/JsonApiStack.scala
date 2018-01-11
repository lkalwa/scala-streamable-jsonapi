package package com.github.lkalwa.scala_streamable_jsonapi

import org.codehaus.jackson.JsonToken
import scala.collection.mutable.ArrayStack

class JsonApiStack {
  private val stack = new ArrayStack[JsonToken]()

  def pop: JsonToken = stack.pop

  def push(elem: JsonToken): Unit = stack.push(elem)

  def topLevelMember: Boolean = stack.toList == List(JsonToken.START_OBJECT)

  def topLevelMemberArray = stack.toList == List(JsonToken.START_ARRAY, JsonToken.START_OBJECT)

  def inTopLevelMemberArray =
    stack.toList == List(JsonToken.START_OBJECT, JsonToken.START_ARRAY, JsonToken.START_OBJECT)

  def singleTopLevelObject = stack.toList == List(JsonToken.START_OBJECT, JsonToken.START_OBJECT)
}
