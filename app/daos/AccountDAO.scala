package daos

/**
 * Created by vinaysaini on 9/14/15.
 */

import play.api.mvc.RequestHeader

import scala.concurrent.{Await, Future}

import models.Account
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import org.mindrot.jbcrypt.BCrypt
import scala.concurrent.duration._

class AccountDAO extends HasDatabaseConfig[JdbcProfile] {

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  private val Accounts = TableQuery[AccountsTable]

  def authenticate( email: String, password: String ): Option[Account] = {
    val acc: Option[Account] = Await.result(findByEmail( email ), Duration.Inf)
    acc.filter { account => BCrypt.checkpw( password, account.password) }
  }

  def findByEmail( email: String ): Future[Option[Account]] =
    db.run( Accounts.filter( _.email === email ).result.headOption )

  def findById(id: Long): Future[Option[Account]] =
    db.run(Accounts.filter(_.id === id).result.headOption)

  def findAll(): List[Account] = Await.result(db.run(Accounts.result).map(_.toList), Duration.Inf)

  def insert(account: Account) = {
    val pswd = BCrypt.hashpw(account.password, BCrypt.gensalt())
    db.run(Accounts += Account(account.id, account.email, pswd, account.name, account.role.toString))
  }

  private class AccountsTable(tag: Tag) extends Table[Account](tag, "account") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def password = column[String]("password")
    def name = column[String]("name")
    def role = column[String]("role")

    def * = (id.?, email, password, name, role) <> (Account.tupled, Account.unapply _)
  }
}