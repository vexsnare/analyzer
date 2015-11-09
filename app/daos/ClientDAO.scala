package daos

import models.{Account, Client}
import play.api.Play
import slick.driver.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by vinaysaini on 10/18/15.
 */
class ClientDAO extends HasDatabaseConfig[JdbcProfile] {

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  val Clients = TableQuery[ClientsTable]

  def findAll(): List[Client] = Await.result(db.run(Clients.result).map(_.toList), Duration.Inf)

  def insert(client: Client) = db.run(Clients += client)

  class ClientsTable(tag: Tag) extends Table[Client](tag, "client") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def ats = column[String]("ats")
    def displayName = column[String]("displayName")
    def feedId = column[String]("feedId")
    def sourceId = column[String]("sourceId")
    def host = column[String]("host")

    def * = (id.?, name, ats, displayName, feedId, sourceId, host) <> (Client.tupled, Client.unapply _)
  }
}
