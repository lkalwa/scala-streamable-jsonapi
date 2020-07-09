package com.github.lkalwa.scala_streamable_jsonapi

import com.fasterxml.jackson.core.JsonToken
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class JsonApiStackSpec extends AnyFlatSpec with should.Matchers {
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