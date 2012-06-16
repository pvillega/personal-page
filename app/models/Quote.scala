package models

import play.api.Logger
import play.api.cache.Cache
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Stores a quote for the main page header
 */
case class Quote(quote: String) {

  override def toString() = {
    quote
  }
}

/** Used for administration of quotes */
case class QuoteForm(list: List[String])


object Quote {

  /**
   * Initializes the cached structures for the application
   */
  def init() = {
    all()
  }

  /**
   * Returns all links in the system
   */
  def all() = {
    import JsonSupport._

    Logger.info("Quote.all - Loading Quote data")
    Cache.getOrElse("quotes", controllers.Application.cacheStorage){
      Logger.info("Quote.all - Quote data not in cache, loading from file")
      scala.util.Random.shuffle(loadQuotes)
    }
  }

  /**
   * Saves into filesystem the given quotes
   * Only used in dev mode when editing quotes
   * @param list the list of quotes to store
   */
  def save(list: List[Quote]) = {
    Logger.info("Quote.save - saving quotes to json file [%s]".format(list))
    JsonSupport.updateQuotes(list)
  }
}

