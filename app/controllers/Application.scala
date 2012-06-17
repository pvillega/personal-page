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

  //Value used by caches when storing data, to facilitate working in dev environment (in production with no config it defaults to 24h)
  val cacheStorage = Play.configuration.getInt("memcached.time").getOrElse(86400)

  val rssKey = "rss"
  val sitemapKey = "sitemap"

  /**
   * Shows the main page of the application
   */
  def index = Action {
    implicit request =>
      Logger.info("Application.index accessed")
      val projects = Project.homeList()
      val bio = Bio.getBio()
      val posts = Post.homeList()
      val quotes = Quote.all()
      Logger.debug("Application.index - projects [%s] bio [%s]".format(projects, bio))
      Ok(views.html.index(projects, posts, bio, quotes))
  }

  /**
   * Shows the links stored to check later
   */
  def archive() = Action {
    implicit request =>
      Logger.info("Application.archive accessed")
      val unchecked = Link.getAllUnchecked()
      val archived = Link.getArchivedLinks()
      Logger.debug("Application.archive - to show archived[%s] unchecked[%s]".format(archived, unchecked))
      Ok(views.html.application.archive(archived, unchecked))
  }

  /**
   * Shows the projects stored
   */
  def projects() = Action {
    implicit request =>
      Logger.info("Application.projects accessed")
      val projects = Project.all()
      Logger.debug("Application.projects - projects to show [%s]".format(projects))
      Ok(views.html.application.projects(projects))
  }

  /**
   * Shows the full bio page
   */
  def fullBio() = Action {
    implicit request =>
      Logger.info("Application.fullBio accessed")
      val bio = Bio.getFullBio()
      Logger.debug("Application.fullBio - fullBio to show [%s]".format(bio))
      Ok(views.html.application.fullbio(bio))
  }

  /**
   * Shows the blog page (all posts)
   * @param page the page to show
   */
  def blog(page: Int) = Action {
    implicit request =>
      Logger.info("Application.blog accessed for page[%d]".format(page))
      val (posts, previous, next) = Post.page(page)
      Logger.debug("Application.blog - posts to show [%s]".format(posts))
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
      Logger.debug("Application.tagged - posts to show [%s]".format(posts))
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
          Logger.debug("Application.post - post to show [%s]".format(post))
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
  def rss() = Action {
    implicit request =>
      Logger.info("Application.rss accessed")
      val render = Cache.getOrElse(rssKey, cacheStorage) {
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
      val render = Cache.getOrElse(sitemapKey, cacheStorage) {
        Logger.info("Application.sitemap")
        val sitemaps = Sitemap.generateSitemap()
        Logger.debug("Application.sitemap - list to show [%s]".format(sitemaps))
        views.html.sitemap(sitemaps)
      }
      Ok(render).as("application/xml")
  }
}