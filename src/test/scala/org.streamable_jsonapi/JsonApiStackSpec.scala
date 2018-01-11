package com.github.lkalwa.streamable_jsonapi

import org.scalatest._
import org.codehaus.jackson.JsonToken

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