/**
 * Created by vinaysaini on 9/15/15.
 */

import daos.{ClientDAO, AccountDAO}
import models.{Client, Account}
import models.Role.{NormalUser, Administrator}
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val account = new AccountDAO
    val client = new ClientDAO

    if (account.findAll().isEmpty) {
      Seq(
        account.insert(Account(Some(1),"alice@example.com", "secret", "Alice", "Administrator")),
        account.insert(Account(Some(2),"vinay@indeed.com", "secret", "Vinay", "Administrator")),
        account.insert(Account(Some(3), "bob@example.com",   "secret", "Bob",   "NormalUser"))
      )
    }
    if (client.findAll().isEmpty) {
      Seq(
        client.insert(Client(Some(1),"globus", "tbe", "Globus Medical", "1234", "43221", "globus_host"))
      )
    }
  }
}
