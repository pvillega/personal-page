package controllers

import play.api._
import cache.Cache
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play.current
import play.api.mvc.BodyParsers._


object Application extends Controller {

  //Admin mail to be made available across app
  val errorReportingMail = Play.configuration.getString("mail.onError").getOrElse("")

  /**
   * Shows the main page of the application
   */
  def index = Action {
    implicit request =>
      Logger.info("Application.index accessed")
      Ok(views.html.index())
  }







  /**
   * Makes some routes available via javascript
   */
  def javascriptRoutes = Action {
    import routes.javascript._

    Ok(Cache.getOrElse("javascriptRoutes", 60*60*24){
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.index
      )
    }
    ).as("text/javascript")
  }

}