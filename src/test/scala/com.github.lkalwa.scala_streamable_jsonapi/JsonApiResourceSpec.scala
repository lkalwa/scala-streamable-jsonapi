package com.github.lkalwa.scala_streamable_jsonapi

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class JsonApiResourceSpec extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach {

  it should "has identity fields" in {
    val jsonApiObj = new JsonApiResource(Map("type"->"users","id"->"10"), new TestHandler())
    jsonApiObj.resTypeOption should equal(Option("users"))
    jsonApiObj.idOption should equal(Option("10"))
  }

  it should "populate attributes" in {
    val jsonApiObj = new JsonApiResource(Map("type"->"users","id"->"10","attributes"->Map("name"->"John")), new TestHandler())
    jsonApiObj.attributes should equal(Map("name" -> "John"))
  }

  it should "provide basic validity check" in {
    val handler = new TestHandler()
    val jsonApiObj = new JsonApiResource(Map(), handler)
    handler.errors.map(_("details").toString) should equal(List("missing id or local:id", "missing type"))
    assert(!jsonApiObj.isValid)
  }

  it should "works with array attributes" in {
    val jsonApiObj = new JsonApiResource(Map("type"->"users","id"->"10",
      "attributes"->Map("grades"->List("A","C","D"))), new TestHandler())
    jsonApiObj.attributes("grades") should equal(List("A","C","D").sorted)
  }

  it should "work with has one relationships" in {
    val jsonApiObj = new JsonApiResource(Map("type"->"users","id"->"10",
      "relationships"->Map("employee"->Map("data"->Map("type"->"employees","id"->"1")))), new TestHandler())
    jsonApiObj.relationships("employee").head.map(_.isValid should equal(true))
  }

  it should "works with has many relationships" in {
    val jsonApiObj = new JsonApiResource(Map("type" -> "users", "id" -> "10",
      "relationships" ->
        Map("locations" -> Map("data" -> List(Map("type" -> "locations", "id" -> "1"), Map("type" -> "locations", "id" -> "2"))))),
      new TestHandler()
    )
    jsonApiObj.relationships("locations").map { listWithRels =>
      listWithRels.size should equal(2)
    }
  }

  it should "represent also empty and nullified relationships" in {
    val jsonApiObj = new JsonApiResource(Map("type"->"users","id"->"10",
    "relationships"->
      Map("locations"->Map("data"->List(Map("type"->"locations","id"->"1"), Map("type"->"locations","id"->"2"))),
        "employee"->Map("data"->Map("type"->"employees","id"->"1")),
        "tasks"->Map("data"->List()),
        "manager"->Map("data"->null))), new TestHandler()
    )

    val rels = jsonApiObj.relationships
    rels("locations").map(_.size should equal(2))
    rels("employee").map(_.size should equal(1))
    rels("tasks").map(_ should equal(List()))
    rels("manager") should equal(Option.empty)
  }

}
