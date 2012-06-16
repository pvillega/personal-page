package models

import java.util.Date
import play.api.Logger
import play.api.cache.Cache
import play.api.Play.current
import play.api.mvc.{Request, AnyContent}

/**
 * Stores information on a link for a sitemap
 * @param url the link to add to the sitemap
 * @param changeFreq how often is the link updated. Default: Monthly
 * @param priority priority of the link (relevance). Default "0.5"
 */
case class Sitemap(url: String, changeFreq : String = "Monthly", priority: String = "0.5" )

object Sitemap {

  val year = new java.text.SimpleDateFormat("yyyy")
  val month = new java.text.SimpleDateFormat("MM")
  val day = new java.text.SimpleDateFormat("dd")

  /**
   * Returns a list of Sitemap with all the info relevant to the system
   */
  def generateSitemap()(implicit request: Request[AnyContent]) = {
    Logger.info("Sitemap.generateSitemap - Loading sitemap")
    Cache.getOrElse("sitemapXML", controllers.Application.cacheStorage){
      Logger.info("Sitemap.generateSitemap - sitemap data not in cache, generating")

      var list : List[Sitemap] = Nil
      //load relevant urs. Urls not added here are ignored
      list = new Sitemap(url = controllers.routes.Application.index.absoluteURL(), priority = "1" ) :: list
      list = new Sitemap(url = controllers.routes.Application.fullBio.absoluteURL(), priority = "1" ) :: list
      list = new Sitemap(url = controllers.routes.Application.projects.absoluteURL(), priority = "0.9" ) :: list
      list = new Sitemap(url = controllers.routes.Application.archive.absoluteURL(), changeFreq = "Daily") :: list
      list = new Sitemap(url = controllers.routes.Application.rss.absoluteURL(), priority = "1" ) :: list
      list = new Sitemap(url = controllers.routes.Application.sitemap.absoluteURL()) :: list

      // add url's related to blog
      val allPosts = Post.all()
      val pages = (allPosts.size.toDouble/Post.pageSize).ceil.toInt
      //blog pages
      (0 to pages).map { i =>
        list = new Sitemap(url = controllers.routes.Application.blog(i).absoluteURL(), priority = "1" ) :: list
      }
      //posts list
      allPosts.map { post =>
        list = new Sitemap(url = controllers.routes.Application.post(post.id, post.slug, year.format(post.published), month.format(post.published), day.format(post.published)).absoluteURL(), priority = "1" ) :: list
      }
      //tag searches
      val allTags = Post.tagMap()
      allTags.map { case (tag, posts) =>
          val pages = (posts.size.toDouble/Post.pageSize).ceil.toInt
          (0 to pages).map { i =>
            list = new Sitemap(url = controllers.routes.Application.tagged(tag, i).absoluteURL(), priority = "0.6" ) :: list
          }
      }
      list.toList
    }
  }

}
