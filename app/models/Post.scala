package models

import java.util.Date
import play.api.cache.Cache
import play.api.Logger
import play.api.Play.current
import play.api.templates.Html

/**
 * Stores an element to check later
 *
 * An instance is a post for the site. It's stored in a json file with the following format:
 *
 *  [
 *   {
 *     "id": 1,
 *     "title": "http://www.google.es",
 *     "published": "1-1-2000",
 *     "tags": ["tag1", "tag2"]
 *     "file": "name of markdown file with content"
 *   },
 *   ... more entries ...
 *  ]
 *
 * Each entry is an element in the json array. The "file" property contains the content of the post, in markdown.
 *
 * Read http://daringfireball.net/projects/markdown/syntax for a tutorial on Markdown
 *
 */
case class Post(id: Int, title: String, published: Date, tags: Option[Array[String]], file: String) {
  val slug = URLSupport.slugify(title)

  val summary = {
    val content = Post.getContent(this)
    val pos = if (content.size < 500) { content.size
    } else {
      val x = content.indexOf(".", 500)
      if (x == -1) { content.size } else { x + 1 }
    }
    content.substring(0, pos)
  }

  val summaryHtml = Html(MarkdownSupport.convertToHtml(summary))
}

/**
 * Used to store the contents of a new post
 * @param title the title of the post, also used as filename
 * @param date the publication date of the post
 * @param content the markdown content to store
 * @param tags list of tags, comma separated
 */
case class PostText(title: String, date: Option[Date], content: String, tags: String)

object Post {

  val pageSize = 10
  private val pathToPosts = "public/data/posts/"

  private val allKey = "loadPosts"
  private val mapKey = "mapPosts"
  private val tagKey = "tagMapPosts"
  private val homeKey = "postHome"
  private val pageKey = "allPt"
  private val tagPageKey = "tgPt"
  private val postContentKey = "pCnt"
  private val postContentHtmlKey = "pCntHtml"

  /**
   * Loads all posts from disk
   */
  def all() = {
    import JsonSupport._
    Cache.getOrElse(allKey, controllers.Application.cacheStorage){
      Logger.info("Post.loadPost - Posts data not in cache, loading from file")
      val list = loadPosts

      list.sortWith((d1, d2) => d1.published.after(d2.published))
    }
  }

  /**
   * Initializes the cached structures for the application
   */
  def init() = {
    all()
    postsMap()
    tagMap()
    homeList()
    all().map { post => getContentAsHtml(post) }
  }

  /**
   * Stores all the posts referenced by id
   */
  def postsMap() = {
    Cache.getOrElse(mapKey, controllers.Application.cacheStorage) {
      Logger.info("Post.postsMap - data not in cache, loading from file")
      val list = all()
      var map = Map.empty[Int, Post]
      list.map { p =>
        map += (p.id -> p)
      }
      Logger.debug("Post.postsMap - data generated [%s]".format(map))
      map
    }
  }

  /**
   * Stores all the posts referenced by tag
   */
  def tagMap() = {
    Cache.getOrElse(tagKey, controllers.Application.cacheStorage) {
      Logger.info("Post.postsMap - data not in cache, loading from file")
      val list = all()
      var map = Map.empty[String, List[Post]]
      list.map { p =>
        p.tags match {
          case Some(tagList) => tagList.map { t =>
            map += ( t -> (p :: map.getOrElse(t, Nil)) )
          }
          case _ =>
        }
      }
      Logger.debug("Post.tagMap - data generated [%s]".format(map))
      map
    }
  }

  /**
   * Returns a list with the latest posts to be used in the homepage
   */
  def homeList() = {
    Logger.info("Post.homeList - Loading Home Posts data")
    Cache.getOrElse(homeKey, controllers.Application.cacheStorage){
      Logger.info("Post.homeList - Posts Home data not in cache, loading from file")
      val list = all()
      list.take(10)
    }
  }

  /**
   * Retrieves the post with the given id
   * @param id the id of the post
   */
  def getById(id: Int) = {
    Logger.info("Post.getById - retrieving post [%d]".format(id))
    Cache.getOrElse("post" + id, controllers.Application.cacheStorage) {
      Logger.info("Post.getById - post [%d] not in cache".format(id))
      postsMap.get(id)
    }
  }

  /**
   * Returns posts in the given page
   * Returns a Tuple that contains:
   *  - list: a list with the Posts in the system that belong to the given page
   *  - previous: true if there are newer elements to show, false otherwise
   *  - next: true if there are older elements to show, false otherwise
   * @param page the page we want to show (each page are 10 posts)
   */
  def page(page: Int) = {
    Logger.info("Post.all - retrieving all posts for page [%d]".format(page))
    Cache.getOrElse(pageKey + page, controllers.Application.cacheStorage) {
      Logger.info("Post.all - all posts for page [%d] not in cache".format(page))
      val list = all()
      val start = if (page*pageSize < list.size) { page*pageSize } else { list.size - 1 }
      val end = if (page*pageSize + pageSize < list.size) { page*pageSize + pageSize } else { list.size }
      val previous = start >= pageSize
      val next = end < list.size
      (list.slice(start, end), previous, next)
    }
  }

  /**
   * Returns posts with given tag in the given page
   * Returns a Tuple that contains:
   *  - list: a list with the Posts in the system that belong to the given page
   *  - previous: true if there are newer elements to show, false otherwise
   *  - next: true if there are older elements to show, false otherwise
   * @param tag the tag we are filtering for
   * @param page the page we want to show (each page are 10 posts)
   */
  def tagged(tag: String, page: Int) = {
    Logger.info("Post.tagged - retrieving all posts for page[%d] tag[%s]".format(page, tag))
    Cache.getOrElse(tagPageKey + page + tag, controllers.Application.cacheStorage) {
      Logger.info("Post.tagged - all posts for page[%d] tag[%s] not in cache".format(page, tag))
      val list = tagMap.getOrElse(tag, Nil)
      val start = if (page*pageSize < list.size) { page*pageSize } else { list.size - 1 }
      val end = if (page*pageSize + pageSize < list.size) { page*pageSize + pageSize } else { list.size }
      val previous = start >= pageSize
      val next = end < list.size
      (list.slice(start, end), previous, next)
    }
  }


  /**
   * Retrieves the markdown content of the given post
   * @param post the post from which we want the content
   */
  def getContent(post: Post)= {
    Logger.info("Post.getContent - retrieving content of post with id [%d]".format(post.id))
    Cache.getOrElse(postContentKey+post.id, controllers.Application.cacheStorage) {
      Logger.info("Post.getContent - content not in cache for post with id [%d], loading".format(post.id))
      MarkdownSupport.loadMarkdown(pathToPosts + post.file)
    }
  }

  /**
   * Returns the content as HTML (instead of markdown)
   * Used for RSS feeds
   * @param post the post of which we want the content
   */
  def getContentAsHtml(post: Post) = {
    Logger.info("Post.getContentAsHtml - retrieving content of post with id [%d] as html".format(post.id))
    Cache.getOrElse(postContentHtmlKey+post.id, controllers.Application.cacheStorage) {
      Logger.info("Post.getContentAsHtml - content as html not in cache for post with id [%d], loading".format(post.id))
      val markdown = getContent(post)
      Html(MarkdownSupport.convertToHtml(markdown))
    }
  }

  /**
   * Saves into filesystem the given post content
   * @param post the post content to store
   */
  def save(post: PostText) = {
    val filename = URLSupport.slugify(post.title) + ".markdown"
    Logger.info("Post.save - saving content of post into file [%s]".format(filename))
    MarkdownSupport.saveMarkdown(pathToPosts + filename, post.content)
    Logger.debug("Post.save - markdown saved, now creating new entry in json file")
    val nextId = if (all().isEmpty) { 0 } else { all().maxBy(_.id).id }
    val newPost = Post(id = nextId + 1, published = post.date.getOrElse(new Date()), title = post.title, file = filename, tags = Some(post.tags.split(",").map( t => t.trim)))
    Logger.debug("Post.save - saving post[%s]".format(newPost))
    val newList = newPost :: all()
    JsonSupport.updatePosts(newList)
  }

  /**
   * Removes the given post and its markdown file
   * @param id the id of the post to remove
   */
  def delete(id: Int) = {
    Logger.info("Post.delete - delete post[%d]".format(id))
    all().find(_.id == id) match  {
      case Some(post) => {
        MarkdownSupport.deleteMarkdown(pathToPosts + post.file)
        val newList = all().filterNot(_.id == post.id)
        JsonSupport.updatePosts(newList)
      }
      case _ => //ignore, do nothing
    }
  }

  /**
   * Updates a post with the given data
   * @param postText the data to save for the post
   * @param id the id of the post to update
   */
  def update(postText: PostText, id: Int) = {
    Logger.info("Post.update - update post[%d]".format(id))
    val post = getById(id).get

    val filename = post.file
    Logger.info("Post.update - saving content of post into file [%s]".format(filename))
    MarkdownSupport.saveMarkdown(pathToPosts + filename, postText.content)

    Logger.debug("Post.update - markdown saved, now updating entry in json file")
    val newPost = post.copy(title = postText.title, file = filename, tags = Some(postText.tags.split(",").map( t => t.trim)))

    Logger.debug("Post.save - saving post[%s]".format(newPost))
    val newList = newPost :: (all().filterNot(_.id == id))
    JsonSupport.updatePosts(newList)
  }
}