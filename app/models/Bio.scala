package models

import play.api.Logger
import play.api.cache.Cache
import play.api.Play.current
import play.api.templates.Html

/**
 * Stores the contents of the Bio markdown file
 * @param full the full bio content
 */
case class Bio(full: String)

/**
 * Returns the bio stored a in markdown document
 */
object Bio {

  val fullBio = "public/data/fullbio.markdown"

  private val fullKey = "fullBio"
  private val fullHtmlKey = "fullBioHtml"

  /**
   * Initializes the cached structures for the application
   */
  def init() = {
    getFullBio()
  }


  def getFullBio() = {
    Logger.info("Bio.getFullBio - Loading Bio")
    Cache.getOrElse(fullKey, controllers.Application.cacheStorage){
      Logger.info("Bio.getFullBio - Bio data not in cache, loading from file")
      MarkdownSupport.loadMarkdown(fullBio)
    }
  }

  def getFullBioHtml() = {
    Logger.info("Bio.getFullBioHtml - Loading Bio as Html")
    Cache.getOrElse(fullHtmlKey, controllers.Application.cacheStorage){
      Logger.info("Bio.getFullBioHtml - Bio data not in cache")
      val markdown = getFullBio()
      Html(MarkdownSupport.convertToHtml(markdown))
    }
  }

  /**
   * Saves into filesystem the given bio
   * @param bio the bio content to store
   */
  def save(bio: Bio) = {
    Logger.info("Bio.save - saving contents")
    MarkdownSupport.saveMarkdown(fullBio, bio.full)
  }

}
