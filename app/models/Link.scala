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
 *     "comment": "a comment",
 *     "archive": true,
 *     "category": "name",
 *     "subcategory": "another name",
 *     "subject": "3rd name"
 *   },
 *   ... more entries ...
 *  ]
 *
 * Each entry is an element in the json array
 * We allow 3 level of categories for easy classification: category, subcategory, subject
 *
 */
case class Link(id: Int, link: String, comment: String, archive: Boolean, category: String, subcategory: String, subject: String)

object Link {

  private val allKey = "lnkData"
  private val archivedKey = "lnkmapArch"
  private val allArchivedKey = "lnkallArch"
  private val uncheckedKey = "lnkUnckd"

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
      list
    }
  }

  /**
   * Returns a Map with all archived items grouped by category and subcategories
   * @return a Map with all archived items grouped by category and subcategories
   */
  def getArchivedLinks() = {
    Logger.info("Link.getArchivedLinks - Loading Links with the archive flag enabled")
    Cache.getOrElse(archivedKey, controllers.Application.cacheStorage){
      val category = all().filter(_.archive).groupBy(_.category)
      val subcategory = category.map{ entry => (entry._1, entry._2.groupBy(_.subcategory)) }
      val subject = subcategory.map{ entry => (entry._1, entry._2.map { sc => (sc._1, sc._2.groupBy(_.subject))} ) }
      subject
    }
  }

  /**
   * Returns a list with all archived links in the system
   * @return a list with all archived links in the system
   */
  def getAllArchived() = {
    Logger.info("Link.getAllArchived - Loading all archived links")
    Cache.getOrElse(allArchivedKey, controllers.Application.cacheStorage){
      all().filter(_.archive)
    }
  }


  /**
   * Returns a list with all unchecked links in the system
   * @return a list with all unchecked links in the system
   */
  def getAllUnchecked() = {
    Logger.info("Link.getAllUnchecked - Loading all unchecked links")
    Cache.getOrElse(uncheckedKey, controllers.Application.cacheStorage){
      all().filterNot(_.archive)
    }
  }

  /**
   * Returns a list of all subcategories to be used in autocomplete
   * @return a list of all subcategories to be used in autocomplete
   */
  def getAllSubCategories() = {
    all().groupBy(_.subcategory).keySet.mkString("['","','","']")
  }

  /**
   * Returns a list of all subject to be used in autocomplete
   * @return a list of all subject to be used in autocomplete
   */
  def getAllSubjects() = {
    all().groupBy(_.subject).keySet.mkString("['","','","']")
  }

  /**
   * Returns a list of all categories to be used in autocomplete
   * @return a list of all categories to be used in autocomplete
   */
  def getAllCategories() = {
    all().groupBy(_.category).keySet.mkString("['","','","']")
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
   * @return true if the link was saved, false if the link was already in the system
   */
  def save(link: Link) = {
    Logger.info("Link.save - creating new entry in json file")
    val isInSystem = all().exists(l => l.link == link.link)

    if (!isInSystem) {
      val nextId = if (all().isEmpty) { 1 } else { all().maxBy(_.id).id + 1 }
      val newLink = link.copy(id = nextId)
      Logger.debug("Link.save - saving link[%s]".format(newLink))
      val newList = newLink :: all()
      JsonSupport.updateLinks(newList)
    }
    !isInSystem
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
