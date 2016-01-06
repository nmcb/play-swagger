package de.zalando.play.controllers

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.{CsvMapper, CsvParser}
import org.joda.time.{DateMidnight, DateTime}
import play.api.mvc.{PathBindable, QueryStringBindable}

/**
  * @author slasch 
  * @since 03.01.2016.
  */
object PlayPathBindables {

  implicit object pathBindableDateTime extends PathBindable.Parsing[DateTime](
    Rfc3339Util.parseDateTime,
    Rfc3339Util.writeDateTime,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTime: %s".format(key, e.getMessage)
  )

  implicit object pathBindableDateMidnight extends PathBindable.Parsing[DateMidnight](
    Rfc3339Util.parseDate,
    Rfc3339Util.writeDate,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateMidnight: %s".format(key, e.getMessage)
  )
  implicit object queryBindableDateTime extends QueryStringBindable.Parsing[DateTime](
    Rfc3339Util.parseDateTime,
    Rfc3339Util.writeDateTime,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTime: %s".format(key, e.getMessage)
  )

  implicit object queryBindableDateMidnight extends QueryStringBindable.Parsing[DateMidnight](
    Rfc3339Util.parseDate,
    Rfc3339Util.writeDate,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateMidnight: %s".format(key, e.getMessage)
  )

  /**
    * Factory to create PathBindable for optional values of any type
    * @param tBinder
    * @tparam T
    * @return
    */
  def createOptionPathBindable[T](implicit tBinder: PathBindable[T]) = new PathBindable[Option[T]] {
    override def bind(key: String, value: String): Either[String, Option[T]] = {
      val wrap = Option(value).map(tBinder.bind(key, _))
      wrap.map(_.right.map(Option.apply)).getOrElse(Right(None))
    }

    override def unbind(key: String, value: Option[T]): String = value match {
      case None => null
      case Some(v) => tBinder.unbind(key, v)
    }
  }
  /**
    * Factory to create QueryBindable for optional values of any type
    * @param tBinder
    * @tparam T
    * @return
    */
  def createOptionQueryBindable[T](implicit tBinder: QueryStringBindable[T]) = new QueryStringBindable[Option[T]] {
    override def bind(key: String, values: Map[String, Seq[String]]): Option[Either[String, Option[T]]] = {
      val wrap = values.get(key).flatMap(_ => tBinder.bind(key, values))
      wrap.map(_.right.map(Option.apply))
    }

    override def unbind(key: String, value: Option[T]): String = value match {
      case None => null
      case Some(v) => tBinder.unbind(key, v)
    }
  }

  /**
    * Example use for pipe-separated array of Ints
    * @param tBinder  this binder should be available from Play
    * @return
    */
  implicit def createArrayWrapperQueryBindable(implicit tBinder: PathBindable[Int]): PathBindable[ArrayWrapper[Int]] =
    createArrPathBindable(PipesArrayWrapper(Nil))

  /**
    * Factory method for bindables of different types
    *
    * @param wrapper  the wrapper is used to distinguish different separator chars
    * @param tBinder  the binder for underlying types
    * @tparam T       the type of array items
    * @return
    */
  def createArrPathBindable[T](wrapper: ArrayWrapper[T])(implicit tBinder: PathBindable[T]) = new PathBindable[ArrayWrapper[T]] {

    val mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY)
    mapper.schema().withArrayElementSeparator(wrapper.separator).withColumnSeparator('\n').withLineSeparator("\n")
    val reader = mapper.readerFor(classOf[Array[String]])

    def bind(key: String, value: String): Either[String, ArrayWrapper[T]] = try {
      val line = readArray(value)
      val xs = line map { tBinder.bind(key, _) }

      val lefts = xs collect {case Left(x) => x }
      lazy val rights = xs collect {case Right(x) => x}

      lazy val success = wrapper.copy(rights.toSeq)
      if (lefts.isEmpty) Right(success) else Left(lefts.mkString("\n"))
    } catch {
      case e: Exception => Left(e.getMessage)
    }

    /**
      * Unbind method converts an ArrayWrapper to the Path string
 *
      * @param key  parameter name
      * @param w    wrapper to convert
      * @return
      */
    def unbind(key: String, w: ArrayWrapper[T]): String = writeArray(w.items map (tBinder.unbind(key, _)))

    private def readArray(line: String) = {
      val array = reader.readValues(line.getBytes).asInstanceOf[MappingIterator[Array[String]]]
      val resArray = if (array.hasNext) array.next() else Array.empty[String]
      resArray
    }

    private def writeArray(items: Seq[String]): String = mapper.writer().writeValueAsString(items)
  }
}