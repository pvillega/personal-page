package models

import play.api.Logger
import play.api.mvc.{JavascriptLitteral, QueryStringBindable}

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 26/05/12
 * Time: 13:17
 * Common helpers used in the application related to the model
 */

/**
 * Defines a Page of elements to be rendered in a template
 * @param items items in the page
 * @param page page number
 * @param offset page offset
 * @param total total elements
 * @param pageSize max elements in a page
 * @tparam A type of element to render
 */
case class Page[+A](items: Seq[A], page: Int, offset: Long, total: Long, pageSize: Int) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
  lazy val maxPages = (total.toDouble/pageSize).ceil.toInt
  lazy val paginationStart = (page - 2).max(1)
  lazy val paginationEnd = (page + 3).min(maxPages)
}


//TODO: remove when updating to 2.1
object QueryBinders {

  /**
   * QueryString binder for List
   */
  implicit def bindableList[T: QueryStringBindable] = new QueryStringBindable[List[T]] {
    def bind(key: String, params: Map[String, Seq[String]]) = Some(Right(bindList[T](key, params)))
    def unbind(key: String, values: List[T]) = unbindList(key, values)
  }

  private def bindList[T: QueryStringBindable](key: String, params: Map[String, Seq[String]]): List[T] = {
    if(Logger.isDebugEnabled){
      Logger.debug("QueryBinders.bindList [%s | %s]".format(key, params))
    }
    for {
      listKey <- params.keys.filter(_.startsWith(key)).toList
      values <- params.get(listKey).filterNot(_.isEmpty).toList
      rawValue <- values
      bound <- implicitly[QueryStringBindable[T]].bind(key, Map(key -> Seq(rawValue)))
      value <- bound.right.toOption
    } yield value
  }

  private def unbindList[T: QueryStringBindable](key: String, values: Iterable[T]): String = {
    (for (value <- values) yield {
      implicitly[QueryStringBindable[T]].unbind(key, value)
    }).mkString("&")
  }

  /**
   * Convert a Scala List[T] to Javascript array
   */
  implicit def literalList[T](implicit jsl: JavascriptLitteral[T]) = new JavascriptLitteral[List[T]] {
    def to(value: List[T]) = value match {
      case l: List[_] if !l.isEmpty => "[" + value.map { v => jsl.to(v)+"," } +"]"
      case _ => "[]"
    }
  }

}
