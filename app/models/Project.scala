package models

import java.util.Date
import play.api.Logger
import play.api.cache.Cache
import play.api.Play.current
import com.github.mumoshu.play2.memcached.MemcachedPlugin

/**
 * Stores an element to check later
 *
 * An instance is something that has been done (portfolio?). It's stored in a json file with the following format:
 *
 *  [
 *   {
 *     "id": 1,
 *     "name": "project title",
 *     "image": "path to local image to show",
 *     "link": "http://www.google.es",
 *     "added": "1-1-2000",
 *     "comment": "a markdown comment",
 *     "status": "a string explaining the status"
 *   },
 *   ... more entries ...
 *  ]
 *
 * Each entry is an element in the json array. Images are all loaded relative from "public/images/projects/"
 *
 */
case class Project(id: Int, name: String, image: String, link: String, added: Date, comment: String, status: String)

object Project {

  private val allKey = "projects"
  private val homeKey = "projectsHome"

  /**
   * Initializes the cached structures for the application
   */
  def init() = {
    play.api.Play.current.plugin[MemcachedPlugin].get.api.remove(allKey)
    play.api.Play.current.plugin[MemcachedPlugin].get.api.remove(homeKey)
    all()
    homeList()
  }

  /**
   * Returns a list with all Projects in the system
   */
  def all() = {
    import JsonSupport._

    Logger.info("Project.all - Loading Projects data")
    Cache.getOrElse(allKey, controllers.Application.cacheStorage){
      Logger.info("Project.all - Project data not in cache, loading from file")
      val list = loadProjects()

      list.sortWith((p1, p2) => p1.added.after(p2.added))
    }
  }

  /**
   * Returns a list with the 5 latest projects to be used in the homepage
   */
  def homeList() = {
    Logger.info("Project.homeList - Loading Home Projects data")
    Cache.getOrElse(homeKey, controllers.Application.cacheStorage){
      Logger.info("Project.homeList - Projects Home data not in cache, loading from file")
      val list = all()
      list.take(5)
    }
  }

  /**
   * Retrieves the project with the given id
   * Only used in dev mode when editing projects
   * @param id the id of the project
   */
  def getById(id: Int) = {
    Logger.info("Project.getById - retrieving post [%d]".format(id))
    all().filter(_.id == id).head
  }

  /**
   * Saves into filesystem the given project content
   * Only used in dev mode when editing projects
   * @param project the project content to store
   */
  def save(project: Project) = {
    Logger.info("Project.save - creating new entry in json file")
    val nextId = if (all().isEmpty) { 1 } else { all().maxBy(_.id).id + 1 }
    val newProject = project.copy(id = nextId)

    Logger.debug("Project.save - saving project[%s]".format(newProject))
    val newList = newProject :: all()
    JsonSupport.updateProjects(newList)
  }

  /**
   * Removes the given project
   * Only used in dev mode when editing projects
   * @param id the id of the project to remove
   */
  def delete(id: Int) = {
    Logger.info("Project.delete - delete project[%d]".format(id))
    all().find(_.id == id) match  {
      case Some(project) => {
        val newList = all().filterNot(_.id == project.id)
        JsonSupport.updateProjects(newList)
      }
      case _ => //ignore, do nothing
    }
  }

  /**
   * Updates a project with the given data
   * Only used in dev mode when editing projects
   * @param project the data to save for the project
   */
  def update(project: Project) = {
    Logger.info("Project.update - update project[%d]".format(project.id))

    val newList = project :: (all().filterNot(_.id == project.id))
    JsonSupport.updateProjects(newList)
  }
}

