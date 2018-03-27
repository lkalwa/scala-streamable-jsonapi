package com.github.lkalwa.scala_streamable_jsonapi

import org.scalatest._

class JsonApiHandlerSpec extends FlatSpec with Matchers {
  class Handler extends JsonApiHandler

  val handler = new Handler

  "Instance of handler" should "not throw any errors" in {
    noException should be thrownBy handler.startDocument
    noException should be thrownBy handler.endDocument
    noException should be thrownBy handler.startData
    noException should be thrownBy handler.endData
    noException should be thrownBy handler.data(Map())
    noException should be thrownBy handler.startIncluded
    noException should be thrownBy handler.endIncluded
    noException should be thrownBy handler.resource(Map())
    noException should be thrownBy handler.startErrors
    noException should be thrownBy handler.endErrors
    noException should be thrownBy handler.error(Map())
    noException should be thrownBy handler.meta(Map())
    noException should be thrownBy handler.jsonapi(Map())
    noException should be thrownBy handler.links(Map())
  }
}