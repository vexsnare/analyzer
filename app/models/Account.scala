package models

case class Account(id: Option[Long] = None, email: String, password: String, name: String, role: String)