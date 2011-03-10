package sample.snippet 

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.sitemap._
import Helpers._
import _root_.net.liftweb.sitemap.Loc._

import sample.model.User

/* I'm too lazy to define the following models. ;-( */
case class ObjectId(cid: String)
object ObjectId { def isValid(id: String) = true }
case class Checkin(oid: ObjectId)
object Checkin {
  def find = new {
    def byId(user: Long, oid: ObjectId) = new {
      def get = Full(Checkin(oid))
    }
  }
}

case class CheckinPageData(user: User, checkin: Checkin) extends PageData

abstract class AbstractUserPageLoc[Data <: PageData]
         extends ItemRewriteLoc[User, Data]

object CheckinPage extends AbstractUserPageLoc[CheckinPageData] {
  override val name = "checkin"
  override val pathPrefix = "user" :: Nil
  override def link = new Link(pathPrefix ++ List("checkin"))
  override def getItem(id: String) = User.find(id)
  override def wrapItem(userBox: Box[User]) = Empty

  override def finishPath(userBox: => Box[User],
                          pathSuffix: List[String]) = pathSuffix match {
    case "checkin" :: cidStr :: Nil if ObjectId.isValid(cidStr) =>
      for {
        u <- userBox
        c <- Checkin.find.byId(u.id.is, new ObjectId(cidStr)).get
      } yield new CheckinPageData(u, c)
    case _ => Empty
  }
}

class CheckinPage(data: CheckinPageData) {
  def snippet1 = 
    ".snippet" #> <span>snippet1: {data.checkin}, hashCode: {data.hashCode}</span>  
  def snippet2 = 
    ".snippet" #> <span>snippet2: {data.checkin}, hashCode: {data.hashCode}</span>  
  def snippet3 = 
    ".snippet" #> <span>snippet3: {data.checkin}, hashCode: {data.hashCode}</span>  
}
