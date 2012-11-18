package models

import play.api.Logger
import play.api.cache.Cache
import play.api.Play.current

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

  /**
   * Saves into filesystem the given bio
   * @param bio the bio content to store
   */
  def save(bio: Bio) = {
    Logger.info("Bio.save - saving contents")
    MarkdownSupport.saveMarkdown(fullBio, bio.full)
  }

}
