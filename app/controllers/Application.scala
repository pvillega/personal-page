package controllers

import play.api._
import cache.Cache
import play.api.mvc._
import play.api.Play.current
import models._
import play.api.Play.current

//TODO: unit testing (we don't need sessions, we can do it now!
object Application extends Controller {

  //Admin mail to be made available across app
  val errorReportingMail = Play.configuration.getString("mail.onError").getOrElse("")

  //Value used by caches when storing data, to facilitate working in dev environment (override in production)
  val cacheStorage = Play.configuration.getInt("memcached.time").getOrElse(315360000)

  /**
   * Shows the main page of the application
   */
  def index = Action {
    implicit request =>
      Logger.info("Application.index accessed")
      val render = Cache.getOrElse("index", cacheStorage) {
        Logger.info("Application.index not in cache")
        val projects = Project.homeList()
        val bio = Bio.getBio()
        val posts = Post.homeList()
        val quotes = Quote.all()
        Logger.debug("Application.index - projects [%s] bio [%s]".format(projects, bio))
        views.html.index(projects, posts, bio, quotes)
      }
      Ok(render)
  }


  //TODO: review texts!
  //TODO: verify that restart in heroku cleans cache

  /**
   * Shows the links stored to check later
   */
  def archive() = Action {
    implicit request =>
      Logger.info("Application.archive accessed")
      val render = Cache.getOrElse("archive", cacheStorage) {
        Logger.info("Application.archive not in cache")
        val unchecked = Link.getAllUnchecked()
        val archived = Link.getArchivedLinks()
        Logger.debug("Application.archive - to show archived[%s] unchecked[%s]".format(archived, unchecked))
        views.html.application.archive(archived, unchecked)
      }
      Ok(render)
  }

  /**
   * Shows the projects stored
   */
  def projects() = Action {
    implicit request =>
      Logger.info("Application.projects accessed")
      val render = Cache.getOrElse("projects", cacheStorage) {
        Logger.info("Application.projects not in cache")
        val projects = Project.all()
        Logger.debug("Application.projects - projects to show [%s]".format(projects))
        views.html.application.projects(projects)
      }
      Ok(render)
  }

  /**
   * Shows the full bio page
   */
  def fullBio() = Action {
    implicit request =>
      Logger.info("Application.fullBio accessed")
      val render = Cache.getOrElse("fullBio", cacheStorage) {
        Logger.info("Application.fullBio not in cache")
        val bio = Bio.getFullBio()
        Logger.debug("Application.fullBio - fullBio to show [%s]".format(bio))
        views.html.application.fullbio(bio)
      }
      Ok(render)
  }

  /**
   * Shows the blog page (all posts)
   * @param page the page to show
   */
  def blog(page: Int) = Action {
    implicit request =>
      Logger.info("Application.blog accessed for page[%d]".format(page))
      val render = Cache.getOrElse("blog" + page, cacheStorage) {
        Logger.info("Application.blog for page[%d] not in cache".format(page))
        val (posts, previous, next) = Post.page(page)
        Logger.debug("Application.blog - posts to show [%s]".format(posts))
        views.html.application.posts(posts, previous, next, page)
      }
      Ok(render)
  }

  /**
   * Shows all posts with the given tag
   * @param tag the tag used to filter posts by
   * @param page the page to show
   */
  def tagged(tag: String, page: Int) = Action {
    implicit request =>
      Logger.info("Application.tagged accessed for tag[%s] page[%d]".format(tag, page))
      val render = Cache.getOrElse("tagApp" + page + tag, cacheStorage) {
        Logger.info("Application.tagged for tag[%s] page[%d] not in cache".format(tag, page))
        val (posts, previous, next) = Post.tagged(tag, page)
        Logger.debug("Application.tagged - posts to show [%s]".format(posts))
        views.html.application.tagged(posts, previous, next, page, tag)
      }
      Ok(render)
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
      val render = Cache.getOrElse("postApp" + id + year + month + day, cacheStorage) {
        Logger.info("Application.post for id[%d] slug[%s][%s-%s-%s] not in cache".format(id, slug, year, month, day))
        Post.getById(id) match {
          case Some(post) => {
            Logger.debug("Application.post - post to show [%s]".format(post))
            Some(views.html.application.post(post))
          }
          case _ => {
            Logger.warn("Application.post - post [%d] not found".format(id))
            None
          }
        }
      }
      render match {
        case Some(r) => Ok(r)
        case _ => NotFound(views.html.errors.error404(request.path))
      }
  }

  /**
   * Returns the RSS feed of the blog
   */
  def rss() = Action {
    implicit request =>
      Logger.info("Application.rss accessed")
      val render = Cache.getOrElse("rss", cacheStorage) {
        Logger.info("Application.rss")
        val posts = Post.all()
        Logger.debug("Application.rss - posts to show [%s]".format(posts))
        views.html.rss(posts)
      }
      Ok(render).as("application/rss+xml")
  }

  /**
   * Returns the sitemap file
   */
  def sitemap() = Action {
    implicit request =>
      Logger.info("Application.sitemap accessed")
      val render = Cache.getOrElse("sitemap", cacheStorage) {
        Logger.info("Application.sitemap")
        val sitemaps = Sitemap.generateSitemap()
        Logger.debug("Application.sitemap - list to show [%s]".format(sitemaps))
        views.html.sitemap(sitemaps)
      }
      Ok(render).as("application/xml")
  }
}