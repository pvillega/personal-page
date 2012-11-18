package models

import play.api.Logger
import scala.Array
import play.api.libs.json._
import java.util.Scanner
import java.io.{FileReader, FileWriter, File}
import play.api.templates.Html

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Support object for Json conversions to/from Model and to manage the json index files
 */
object JsonSupport {

  val postsPath = "public/data/posts/list.json"
  val projectsPath = "public/data/projects/list.json"
  val linksPath = "public/data/dump/list.json"

  /**
   * Stores the list of Posts into the given file as json
   * @param list the list of post to store
   */
  def updatePosts(list: List[Post]) = {
    saveDataToJson(postsPath, Json.toJson(list).toString())
  }

  /**
   * Recovers all the posts from the json file
   */
  def loadPosts() = {
    val json = loadJsonFromFile(postsPath)
    json.as[List[Post]]
  }

  /**
   * Implicit conversion from Post to Json
   */
  implicit object postToJson extends Format[Post] {
    val dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")

    def writes(o: Post): JsValue = {
      val array = o.tags match {
        case Some(tags) => {
          tags.map{ t =>
            JsString(t)
          }
        }
        case _ =>  Array(JsString(""))
      }
      JsObject(
        List(
          "id" -> JsNumber(o.id),
          "title" -> JsString(o.title),
          "published" -> JsString(dateFormat.format(o.published)),
          "tags" -> JsArray(array),
          "file" -> JsString(o.file)
        )
      )
    }

    def reads(json: JsValue): Post = Post(
      id = (json \ "id").as[Int],
      title = (json \ "title").as[String],
      published = dateFormat.parse((json \ "published").as[String]),
      tags =(json \ "tags").as[Option[Array[String]]],
      file = (json \ "file").as[String]
    )
  }



  /**
   * Stores the list of projects in the given file as Json
   * @param list the list of projects to store
   */
  def updateProjects(list: List[Project]) = {
    saveDataToJson[Project](projectsPath, Json.toJson(list).toString())
  }

  /**
   * Recovers all the projects from the json file
   */
  def loadProjects() = {
    val json = loadJsonFromFile(projectsPath)
    json.as[List[Project]]
  }

  /**
   * Implicit conversion from Project to Json
   */
  implicit object projectToJson extends Format[Project] {
    val dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")

    def writes(o: Project): JsValue = JsObject(
      List(
        "id" -> JsNumber(o.id),
        "name" -> JsString(o.name),
        "image" -> JsString(o.image),
        "link" -> JsString(o.link),
        "added" -> JsString(dateFormat.format(o.added)),
        "comment" -> JsString(o.comment),
        "status" -> JsString(o.status)
      )
    )

    def reads(json: JsValue): Project = Project(
      id = (json \ "id").as[Int],
      name = (json \ "name").as[String],
      image = (json \ "image").as[String],
      link = (json \ "link").as[String],
      added = dateFormat.parse((json \ "added").as[String]),
      comment = (json \ "comment").as[String],
      status = (json \ "status").as[String],
      commentHtml = Html(MarkdownSupport.convertToHtml((json \ "comment").as[String])),
      statusHtml = Html(MarkdownSupport.convertToHtml((json \ "status").as[String]))
    )
  }

  /**
   * Stores the list of links in the given file as Json
   * @param list the list of links to store
   */
  def updateLinks(list: List[Link]) = {
    saveDataToJson[Link](linksPath, Json.toJson(list).toString())
  }

  /**
   * Recovers all the links from the json file
   */
  def loadLinks() = {
    val json = loadJsonFromFile(linksPath)
    json.as[List[Link]]
  }

  /**
   * Implicit conversion from Link to Json
   */
  implicit object dumpToJson extends Format[Link] {
    val dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")

    def writes(o: Link): JsValue = JsObject(
      List(
        "id" -> JsNumber(o.id),
        "link" -> JsString(o.link),
        "comment" -> JsString(o.comment),
        "archive" -> JsBoolean(o.archive),
        "category" -> JsString(o.category),
        "subcategory" -> JsString(o.subcategory),
        "subject" -> JsString(o.subject)
      )
    )

    def reads(json: JsValue): Link = Link(
      id = (json \ "id").as[Int],
      link = (json \ "link").as[String],
      comment =(json \ "comment").as[String],
      archive = (json \ "archive").as[Boolean],
      category = (json \ "category").as[String],
      subcategory = (json \ "subcategory").as[String],
      subject = (json \ "subject").as[String]
    )
  }

  /**
   * Saves the given data as json on the given file
   * @param path path of the file to save
   * @param data data to save
   */
  private def saveDataToJson[A](path: String, data: String) = {
    val f = new File(path)
    Logger.debug("JsonSupport.saveDataToJson - saving file [%s] and json [%s]".format(f.getAbsolutePath, data))
    val writer = new FileWriter(f)
    writer.write(data)
    writer.close()
  }

  /**
   * Returns a String with the json contents of the given file
   *
   * @param filePath the path fo the file to read
   */
  private def loadJsonFromFile(filePath : String) = {
    Logger.info("JsonSupport.loadJsonFromFile - Loading data from: %s".format(filePath))
    val source = new File(filePath)
    val scanner = new Scanner(new FileReader(source));
    val buffer = new StringBuilder
    while ( scanner.hasNextLine() ){
      buffer.append(scanner.nextLine())
    }
    Logger.debug("JsonSupport.loadJsonFromFile - Data loaded from %s is %s".format(filePath, buffer.toString))
    val json = Json.parse(buffer.toString)
    Logger.debug("JsonSupport.loadJsonFromFile - Data loaded from %s as json is %s".format(filePath, json.toString))
    json
  }
}