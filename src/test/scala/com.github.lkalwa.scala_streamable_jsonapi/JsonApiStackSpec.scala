package com.github.lkalwa.scala_streamable_jsonapi

import org.scalatest._
import com.fasterxml.jackson.core.JsonToken

class JsonApiStackSpec extends FlatSpec {
  val stack = new JsonApiStack()

  "JsonApiStack" should "return current location" in {
    stack.push(JsonToken.START_OBJECT)
    assert(stack.topLevelMember)
    stack.push(JsonToken.START_OBJECT)
    assert(stack.singleTopLevelObject)
    assert(!stack.topLevelMemberArray)
    stack.pop
    stack.push(JsonToken.START_ARRAY)
    assert(stack.topLevelMemberArray)
  }
}