package controllers
import daos.AccountDAO
import jp.t2v.lab.play2.auth.LoginLogout
import models.Account
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Application extends Controller with LoginLogout with AuthConfigImpl {

  override val accountDAO = new AccountDAO

  def index = Action { implicit request =>
    Ok(views.html.index("Indeed"))
  }

  val loginForm = Form {
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(accountDAO.authenticate)(_.map(u => (u.email, ""))).verifying("Invalid email or password", result => result.isDefined)
  }

  val signUpForm: Form[Account] = Form {
    mapping(
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "cPassword" -> nonEmptyText
    )((name, email, pswd, _) => Account(None,email,pswd,name,"NormalUser"))(
        (account: Account) => Some(account.name, account.email, "",""))
}

  def login = Action { implicit request =>
    //new Jobs().getJobList
    Ok(views.html.auth.login(loginForm))
  }

  def newUser = Action { implicit request =>
    //new Jobs().getJobList
    Ok(views.html.auth.signup(signUpForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
           "success" -> "You've been logged out"
    ))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.auth.login(formWithErrors))),
      user => gotoLoginSucceeded(user.get.id.get)
    )
  }

  def signUp = Action.async { implicit request =>
    signUpForm.bindFromRequest.fold(
    hasErrors = {
      formWithError => {
        Future.successful(BadRequest(views.html.auth.signup(formWithError)))
      }
    },
    success = {
      user => {
        new AccountDAO().insert(user)
        Future.successful(Redirect(routes.Application.index()))
      }
    }
    )
  }

  /** Your application's login form.  Alter it to fit your application */
  /*
  val loginForm = Form {
      mapping("email" -> email, "password" -> text)(accountDAO.authenticate)(_.map(u => (u.email, "")))
          .verifying("Invalid email or password", result => result.isDefined)
  }
  */

  /** Alter the login page action to suit your application. */
  /*
  def login = Action { implicit request =>
      Ok(html.login(loginForm))
  }
  */

  /**
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `Future[Result]`,
   * you can add a procedure like the following.
   *
   *   gotoLogoutSucceeded.map(_.flashing(
   *     "success" -> "You've been logged out"
   *   ))
   */
  /*
  def logout = Action.async { implicit request =>
      // do something...
      gotoLogoutSucceeded
  }
  */

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   *
   * Since the `gotoLoginSucceeded` returns `Future[Result]`,
   * you can add a procedure like the `gotoLogoutSucceeded`.
   */
  /*
  def authenticate = Action.async { implicit request =>
      loginForm.bindFromRequest.fold(
          formWithErrors => Future.successful(BadRequest(html.login(formWithErrors))),
          user => gotoLoginSucceeded(user.get.id)
      )
  }
  */
}