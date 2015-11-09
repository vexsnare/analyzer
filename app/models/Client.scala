package models

/**
 * Created by vinaysaini on 10/18/15.
 */
case class Client(id: Option[Int] = None, name: String, ats: String, displayName: String, feedId: String, sourceId: String, host: String)
