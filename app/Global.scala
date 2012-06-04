/**
 * Created by IntelliJ IDEA.
 * User: pvillega
 * Date: 19/02/12
 * Time: 16:25
 * Global settings class
 */

import controllers.Application
import models.Post
import play.api._
import mvc._
import play.api.mvc.Results._
import play.api.Play.current
import com.typesafe.plugin._

object Global extends GlobalSettings {

  /**
   * Executed before start of application
   */
  override def beforeStart(app : play.api.Application) = {
    Logger.info("beforeStart executed for application %s".format(app.mode))
    super.beforeStart(app)

    // Prepare in-memory structures

    Post.init();
  }

  /**
   * Executed on start of application
   * We load DB data in here
   */
  override def onStart(app: Application) {
    Logger.info("onStart executed for application %s".format(app.mode))
    super.onStart(app)
  }

  /**
   * Error in the application
   * We use the same scenario as Not Found to hide options from hackers but we notify ourselves with email
   */
  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger.error("onError executed for request %s".format(request), ex)

    if(play.api.Play.isProd) {
      val mail = use[MailerPlugin].email
      mail.setSubject("Error in Personal Page")
      mail.addRecipient("Administrator <%s>".format(Application.errorReportingMail), Application.errorReportingMail)
      mail.addFrom("Personal Page Admin <noreply@billeteo.com>")
      mail.send( "Error detected in Personal Page. \n Request: \n %s  \n\n Exception: \n %s \n\n %s".format(request, ex.getMessage, ex.getStackTrace.toList.mkString("\n")) )
    }

    InternalServerError(
      views.html.errors.error(ex)(request)
    )
  }

  /**
   * Route not found
   */
  override def onHandlerNotFound(request: RequestHeader) = {
    Logger.error("onHandlerNotFound executed for request %s".format(request))

    NotFound(
      views.html.errors.error404(request.path)(request)
    )
  }

  /**
   * Route was found but we couldn't bind the parameters
   * We use the same scenario as Not Found to hide options from hackers but we notify ourselves with email
   */
  override def onBadRequest(request : RequestHeader, error : scala.Predef.String) = {
    Logger.error("onBadRequest executed for request %s on error %s".format(request, error))

    if(play.api.Play.isProd) {
      val mail = use[MailerPlugin].email
      mail.setSubject("Bad Request in Personal Page")
      mail.addRecipient("Administrator <%s>".format(Application.errorReportingMail), Application.errorReportingMail)
      mail.addFrom("Personal Page Admin <noreply@billeteo.com>")
      mail.send( "Bad Request received in Personal Page\n. Request: %s  \n\n Error: \n %s".format(request, error) )
    }

    NotFound(
      views.html.errors.error404(request.path)(request)
    )
  }

}