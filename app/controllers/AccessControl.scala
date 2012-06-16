package controllers

import play.api.mvc._
import play.api.mvc.Results._
import play.api.{Play, Logger}
import play.api.Play.current
import play.api.mvc.BodyParsers._

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * This trait limits access to methods if we are not in dev mode
 */

trait AccessControl {

  /**
   * Redirect to NotFound if the user is not authorized.
   * It returns a Result to redirect the user to the "not authenticated" destination of choice
   */
  private def onUnauthorized(request: RequestHeader): Result = {
    Logger.warn("Url not available in Prod mode %s".format(request))
    NotFound(views.html.errors.error404(request.path)(request)).withNewSession
  }

  /**
   * Verifies that we are in Dev mode
   * @param p the body parser of this action
   * @param f the action to execute
   * @tparam A content type
   * @return Result of the action
   */
  def isDev[A](p: BodyParser[A])(f: Request[A] => Result) = {
    Action(p) {
      implicit request =>
        if (Play.isDev){
            f(request)
        } else {
          onUnauthorized(request)
        }
    }
  }

  /**
   * Overloaded method to use the default body parser
   */
  def isDev(f: Request[AnyContent] => Result): Action[AnyContent] = {
    isDev(parse.anyContent)(f)
  }

}