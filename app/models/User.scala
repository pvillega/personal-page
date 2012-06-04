package models

import java.util.Date
import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.cache.Cache
import play.Logger


/**
 * Created by IntelliJ IDEA.
 * User: pvillega
 * Date: 03/03/12
 * Time: 11:48
 * Model for users in the application
 */
case class User(id: Pk[Long] = NotAssigned, name: String, disabled: Boolean = false, admin: Boolean = false, created: Date = new Date, lastAccess: Date = new Date)

object User {

  def findById(id: Long) = Some(new User(Id(id), "Name"))
}

