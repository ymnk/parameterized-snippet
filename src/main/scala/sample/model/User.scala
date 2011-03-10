package sample.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"
  override def screenWrap = 
    Full(<lift:surround with="default" at="content"><lift:bind /></lift:surround>)

  override def fieldOrder = 
    List(id, firstName, lastName, email, locale, timezone, password, textArea)

  override def skipEmailValidation = true
}

class User extends MegaProtoUser[User] {
  def getSingleton = User

  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}
