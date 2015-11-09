package controllers.auth

import controllers.AuthConfigImpl
import jp.t2v.lab.play2.auth.{OptionalAuthElement, AuthElement}
import play.api.mvc.Controller

import views.html



/**
 * Created by vinaysaini on 10/18/15.
 */
class Check extends Controller with OptionalAuthElement  with AuthConfigImpl {
  def isLoggedIn = StackAction { implicit request =>
    val optUser = loggedIn

    Ok("hi")
  }
}
