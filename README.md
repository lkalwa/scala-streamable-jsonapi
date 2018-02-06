# Scala streamable jsonapi

## What is jsonapi?

See: http://jsonapi.org

## Introduction

scala-streamable-jsonapi consists of two major components:

1. Parser (JsonApiParser)
2. Generator (JsonApiGenerator)
3. Handler

**Parser** works with Jackson generic parser (which deals with streaming internally),
to return a stream of jsonapi compliant jsons for streamed members (`data`, `included`, `errors`),
or as a simple json objects for (`meta`, `jsonapi`, `links`). `data` member can be streamed or not depending 
if it's an Array of objects or not.

**Generator** provides a interface to write jsons to Jackson streamed writer.

**Handler** works together with parser. It receives data extracted by parser, 
it'a an interface for client to be able to react on events.

## Usage

### Parser

Initialization:

`new JsonApiParser(instance of java.io.InputStream, handlerObject)`

During parsing input stream parser will call methods on instance on handler. 
See handler section below for more.

### Generator

Initialization:

`new JsonapiGenerator(instance of java.io.OutputStream)`

Client software should call appropriate methods on instance of generator and pass data to be written to it.


### Handler

Handler is a interface for parser, it implements following methods:

  ```def startDocument: Any = {}

  def endDocument: Any = {}

  def startData: Any = {}

  def endData: Any = {}

  def data(obj: Map[String, Any]): Any = {}

  def startIncluded: Any = {}

  def endIncluded: Any = {}

  def resource(obj: Map[String, Any]): Any = {}

  def startErrors: Any = {}

  def endErrors: Any = {}

  def error(obj: Map[String, Any]): Any = {}

  def meta(obj: Map[String, Any]): Any = {}

  def jsonapi(obj: Map[String, Any]): Any = {}

  def links(obj: Map[String, Any]): Any = {}
```  

Names correspond to jsonapi members so it's pretty straightforward to understand which one is called when.

**Note**: during streamed parsing of `data` or `included` extracted jsons as `Map[String, Any]` are passed to handler via the `resource` method.
`data` method is only used **IF** `data` section in parsed json is a single json object.



