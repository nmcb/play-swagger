package de.zalando.play.controllers

/**
  * An abstraction over parameters of type Array
  *
  * Needed for parsing as there are many possibilities to encode an array
  * as query parameter or a header
  *
  * @author slasch 
  * @since 03.01.2016.
  */
trait ArrayWrapper[T] {
  def items: Seq[T]
  def separator: Char
}

case class CsvArrayWrapper[T](items: Seq[T]) extends ArrayWrapper[T] {
  val separator = ','
}
case class TsvArrayWrapper[T](items: Seq[T]) extends ArrayWrapper[T] {
  val separator = '\t'
}
case class SsvArrayWrapper[T](items: Seq[T]) extends ArrayWrapper[T] {
  val separator = ' '
}
case class PipesArrayWrapper[T](items: Seq[T]) extends ArrayWrapper[T] {
  val separator = '|'
}
// TODO this supposed to wrap multiple query parameters with same name, with or without []
case class MultiArrayWrapper[T](items: Seq[T]) extends ArrayWrapper[T] {
  val separator = '?'
}
object ArrayWrapper {
  def apply[T](format: String): Seq[T] => ArrayWrapper[T] = format.toLowerCase match {
    case "tsv" => TsvArrayWrapper.apply
    case "ssv" => SsvArrayWrapper.apply
    case "pipes" => PipesArrayWrapper.apply
    case "multi" => MultiArrayWrapper.apply
    case "csv" => CsvArrayWrapper.apply
    case other => throw new IllegalArgumentException(s"Array format $other is not supported")
  }
}