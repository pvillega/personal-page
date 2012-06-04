package models

import java.io.File
import play.api.Logger
import java.util.Date


/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 04/06/12
 * Time: 17:21
 * File that contains the models for the application
 */

/* A post in the personal page */
case class Post(title: String, published: Date)

object Post {

  val df = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")
//  lazy val list : List[Post]

  def init() = {

    val list = new File("public/data/posts").listFiles
    list.foreach(f => Logger.info("Located file: %s created on %s ".format(f.getName, df.format(new Date(f.lastModified())) ) ) )

  }

}
