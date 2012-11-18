package controllers

import play.api._
import cache.{Cached, Cache}
import play.api.mvc._
import play.api.Play.current
import models._
import play.api.Play.current

object Application extends Controller {

  //Admin mail to be made available across app
  val errorReportingMail = Play.configuration.getString("mail.onError").getOrElse("")

  //Value used by caches when storing data, to facilitate working in dev environment (in production with no config it defaults to 24h)
  val cacheStorage = Play.configuration.getInt("memcached.time").getOrElse(86400)

  val indexKey = "indexPage"
  val fullBioKey = "fullBioPage"
  val projectsKey = "projectsPage"
  val archiveKey = "archivePage"
  val rssKey = "rssPage"
  val sitemapKey = "sitemapPage"

  /**
   * Shows the main page of the application
   */
  def index = Cached(indexKey, cacheStorage) {
    Action {
      implicit request =>
        Logger.info("Application.index accessed")
        val projects = Project.homeList()
        val posts = Post.homeList()
        if(Logger.isTraceEnabled){
          Logger.trace("Application.index - projects [%s] posts[%s]".format(projects, posts))
        }
        Ok(views.html.index(projects, posts))
    }
  }

  /**
   * Shows the links stored to check later
   */
  def archive() = Cached(archiveKey, cacheStorage) {
    Action {
      implicit request =>
        Logger.info("Application.archive accessed")
        val archived = Link.getArchivedLinks()
        if(Logger.isTraceEnabled){
          Logger.trace("Application.archive - to show archived[%s]".format(archived))
        }
        Ok(views.html.application.archive(archived))
    }
  }

  /**
   * Shows the projects stored
   */
  def projects() = Cached(projectsKey, cacheStorage) {
    Action {
      implicit request =>
        Logger.info("Application.projects accessed")
        val projects = Project.all()
        if(Logger.isTraceEnabled){
          Logger.trace("Application.projects - projects to show [%s]".format(projects))
        }
        Ok(views.html.application.projects(projects))
    }
  }

  /**
   * Shows the full bio page
   */
  def fullBio() = Cached(fullBioKey, cacheStorage) {
    Action {
      implicit request =>
        Logger.info("Application.fullBio accessed")
        val bio = Bio.getFullBioHtml()
        if(Logger.isTraceEnabled){
          Logger.trace("Application.fullBio - fullBio to show [%s]".format(bio))
        }
        Ok(views.html.application.fullbio(bio))
    }
  }

  /**
   * Shows the blog page (all posts)
   * @param page the page to show
   */
  def blog(page: Int) = Action {
    implicit request =>
      Logger.info("Application.blog accessed for page[%d]".format(page))
      val (posts, previous, next) = Post.page(page)
      if(Logger.isTraceEnabled){
        Logger.trace("Application.blog - posts to show [%s]".format(posts))
      }
      Ok(views.html.application.posts(posts, previous, next, page))
  }

  /**
   * Shows all posts with the given tag
   * @param tag the tag used to filter posts by
   * @param page the page to show
   */
  def tagged(tag: String, page: Int) = Action {
    implicit request =>
      Logger.info("Application.tagged accessed for tag[%s] page[%d]".format(tag, page))
      val (posts, previous, next) = Post.tagged(tag, page)
      if(Logger.isTraceEnabled){
        Logger.trace("Application.tagged - posts to show [%s]".format(posts))
      }
      Ok(views.html.application.tagged(posts, previous, next, page, tag))
  }

  /**
   * Retrieves the individual post
   * @param id id of the post - used as identifier
   * @param slug slug of the post - only for url purposes
   * @param year the year of publication - only for url purposes
   * @param month the month of publication - only for url purposes
   * @param day the day of publication - only for url purposes
   * @return
   */
  def post(id: Int, slug: String, year: String, month: String, day: String) = Action {
    implicit request =>
      Logger.info("Application.post accessed for id[%d] slug[%s][%s-%s-%s]".format(id, slug, year, month, day))
      Post.getById(id) match {
        case Some(post) => {
          if(Logger.isTraceEnabled){
            Logger.trace("Application.post - post to show [%s]".format(post))
          }
          Ok(views.html.application.post(post))
        }
        case _ => {
          Logger.warn("Application.post - post [%d] not found".format(id))
          NotFound(views.html.errors.error404(request.path))
        }
      }
  }

  /**
   * Returns the RSS feed of the blog
   */
  def rss() = Cached(rssKey, cacheStorage) {
    Action {
      implicit request =>
        Logger.info("Application.rss accessed")
        val posts = Post.all()
        if(Logger.isTraceEnabled){
          Logger.trace("Application.rss - posts to show [%s]".format(posts))
        }
        Ok(views.html.rss(posts)).as("application/rss+xml")
    }
  }

  /**
   * Returns the sitemap file
   */
  def sitemap() = Cached(sitemapKey, cacheStorage) {
    Action {
      implicit request =>
        Logger.info("Application.sitemap accessed")
        val sitemaps = Sitemap.generateSitemap()
        if(Logger.isTraceEnabled){
          Logger.trace("Application.sitemap - list to show [%s]".format(sitemaps))
        }
        Ok(views.html.sitemap(sitemaps)).as("application/xml")
    }
  }
}