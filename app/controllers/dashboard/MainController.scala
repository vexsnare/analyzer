package controllers.dashboard

import controllers.{AuthConfigImpl, Application}
import daos.ClientDAO
import jp.t2v.lab.play2.auth.{AuthElement, LoginLogout}
import models.Client
import models.Role.{Administrator, NormalUser}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

import play.api.Play.current
import play.api.i18n.Messages.Implicits._


/**
 * Created by vinaysaini on 10/17/15.
 */
class MainController extends Controller with AuthElement  with AuthConfigImpl  {

  val clientForm: Form[Client] = Form (
    mapping(
    "id" -> optional(number),
    "name" -> nonEmptyText,
    "ats" -> nonEmptyText,
    "displayName" -> nonEmptyText,
    "feedId" -> nonEmptyText,
    "sourceId" -> nonEmptyText,
    "host" -> text
    ) (Client.apply)(Client.unapply _)
  )
  def index = StackAction(AuthorityKey -> NormalUser) { implicit request =>
      //val user = loggedIn
      val clients = new ClientDAO().findAll()
      Ok(views.html.dashboard.index("Dashboard", clients))
  }

  def createClient = StackAction(AuthorityKey -> Administrator) { implicit request =>
    Ok(views.html.dashboard.createClient("create",clientForm))
  }

  def create = StackAction(AuthorityKey -> Administrator) { implicit request =>
    clientForm.bindFromRequest.fold(
    formWithErrors => BadRequest(views.html.dashboard.createClient("create", formWithErrors)),
    newClient => {
      new ClientDAO().insert(newClient)
      Redirect(controllers.dashboard.routes.MainController.index)
    }
    )
  }
}
