package models

import play.api.Play.current
import play.api.Logger
import java.util.Date
import play.api.cache.Cache

/**
 * Stores an element to check later
 *
 * An instance is something to be checked in the future. It's stored in a json file with the following format:
 *
 *  [
 *   {
 *     "id": 1,
 *     "link": "http://www.google.es",
 *     "added": "1-1-2000",
 *     "comment": "a comment",
 *     "done": false,
 *     "archive": true,
 *     "category": "name"
 *   },
 *   ... more entries ...
 *  ]
 *
 * Each entry is an element in the json array
 *
 */
case class Link(id: Int, link: String, added: Date, comment: String, done: Boolean, archive: Boolean, category: String )

object Link {

  private val allKey = "dumpData"
  private val archivedKey = "dumpArchived"
  private val uncheckedKey = "dumpUnckd"

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

    Logger.info("Link.all - Loading Link data")
    Cache.getOrElse(allKey, controllers.Application.cacheStorage){
      Logger.info("Link.all - Link data not in cache, loading from file")
      val list = loadLinks

      list.sortWith((d1, d2) => d1.added.after(d2.added))
    }
  }

  /**
   * Returns a Map[String, List[Link]] with al archived items grouped by category
   * @return a Tuple with two list of links: unchecked and checked
   */
  def getArchivedLinks() = {
    Logger.info("Link.getArchivedLinks - Loading Links with the archive flag enabled")
      Cache.getOrElse(archivedKey, controllers.Application.cacheStorage){
      all().filter(_.archive).groupBy(_.category)
    }
  }

  /**
   * Returns a list with all unchecked links in the system
   * @return a list with all unchecked links in the system
   */
  def getAllUnchecked() = {
    Logger.info("Link.getAllUnchecked - Loading all unchecked links")
    Cache.getOrElse(uncheckedKey, controllers.Application.cacheStorage){
      all().filterNot(_.done)
    }
  }

  /**
   * Retrieves the link with the given id
   * Only used in dev mode when editing links
   * @param id the id of the project
   */
  def getById(id: Int) = {
    Logger.info("Link.getById - retrieving link [%d]".format(id))
    all().filter(_.id == id).head
  }

  /**
   * Saves into filesystem the given link content
   * Only used in dev mode when editing links
   * @param link the link content to store
   */
  def save(link: Link) = {
    Logger.info("Link.save - creating new entry in json file")
    val nextId = if (all().isEmpty) { 1 } else { all().maxBy(_.id).id + 1 }
    val newLink = link.copy(id = nextId)

    Logger.debug("Link.save - saving link[%s]".format(newLink))
    val newList = newLink :: all()
    JsonSupport.updateLinks(newList)
  }

  /**
   * Removes the given link
   * Only used in dev mode when editing links
   * @param id the id of the link to remove
   */
  def delete(id: Int) = {
    Logger.info("Link.delete - delete link[%d]".format(id))
    all().find(_.id == id) match  {
      case Some(link) => {
        val newList = all().filterNot(_.id == link.id)
        JsonSupport.updateLinks(newList)
      }
      case _ => //ignore, do nothing
    }
  }

  /**
   * Updates a link with the given data
   * Only used in dev mode when editing links
   * @param link the data to save for the link
   */
  def update(link: Link) = {
    Logger.info("Link.update - update link[%d]".format(link.id))

    val newList = link :: (all().filterNot(_.id == link.id))
    JsonSupport.updateLinks(newList)
  }
}
