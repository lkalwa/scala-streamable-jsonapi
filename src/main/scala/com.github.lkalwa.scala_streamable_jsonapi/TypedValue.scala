package com.github.lkalwa.scala_streamable_jsonapi

trait TypedValue {
  def typedGet[T, V](map: Map[String, Any], key: String, transformer: T => V): Option[V] =
    map.get(key).map(t => transformer(t.asInstanceOf[T]))

  def typedStringGet[V](map: Map[String, Any], key: String,
                        transformer: String => V): Option[V] =
    typedGet[String, V](map, key, transformer)
}
