/**
 * Created by IntelliJ IDEA.
 * User: pvillega
 * Date: 19/02/12
 * Time: 16:25
 * Global settings class
 */

import controllers.Application
import models._
import play.api._
import mvc._
import play.api.mvc.Results._
import play.api.Play.current

object Global extends GlobalSettings {

  /**
   * Executed before start of application
   */
  override def beforeStart(app : play.api.Application) = {
    Logger.info("beforeStart executed for application %s".format(app.mode))
    super.beforeStart(app)
  }

  /**
   * Executed on start of application
   */
  override def onStart(app: Application) {
    Logger.info("onStart executed for application %s".format(app.mode))
    super.onStart(app)
    Logger.info("onStart - value fo Application.cacheStorage [%d]".format(Application.cacheStorage))
    Logger.info("onStart - cleaning some caches")
    Logger.info("onStart - forcing items to go into cache")
    Bio.init()
    Link.init()
    Project.init()
    Post.init()
  }

  /**
   * Error in the application
   * We use the same scenario as Not Found to hide options from hackers but we notify ourselves with email
   */
  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger.error("onError executed for request %s".format(request), ex)

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

    NotFound(
      views.html.errors.error404(request.path)(request)
    )
  }

}