package sample.snippet 

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.net.liftweb.http._

import sample.model.User

trait PageData {
  def currentUser: Box[User] = User.currentUser
}

trait ItemRewriteLoc[S, T <: PageData] extends Loc[T] {  
  type MyT = T
  type MyS = S
  
  override def link = new Link[T](pathPrefix)
  override def text = LinkText.strToLinkText(name)
  override def defaultValue: Box[T] = wrapItem(getDefault)

  val pathPrefix: List[String]
 
  def getDefault: Box[S] = Empty
  def getItem(id: String): Box[S]
  def wrapItem(itemBox: Box[MyS]): Box[T]

  /**
   * Override this function to provide a canonical URL
   */
  def canonicalUrl(data: T): Box[String] = Empty
  override def params: List[LocParam[T]] =
    Hidden :: 
    TestValueAccess[T](_.flatMap(canonicalUrl(_))
                        .filter(_ != S.uri)
                        .map(RedirectResponse(_))) :: Nil

  /**
   * By default the path must end after the item id.
   * Override to handle other cases.
   */
  def finishPath(itemBox: => Box[MyS],
                 pathSuffix: List[String]): Box[T] = {
    if (pathSuffix == Nil) wrapItem(itemBox) else Empty
  }

  private class PrefixRewriteRequest(prefix: List[String]) {
    def unapply(in: RewriteRequest): Option[T] = {
      in.path.partPath.splitAt(prefix.length) match {
        case (`prefix`, id :: rest) => {
          // wrapItem(getItem(id))
          finishPath(getItem(id), rest)
        }
        case _ => Empty
      }
    }
  }

  private object ItemRewriteRequest extends PrefixRewriteRequest(pathPrefix)  

  override def rewrite: LocRewrite = Full({
    case ItemRewriteRequest(item) =>
      (RewriteResponse(ParsePath(link.pathList(item), "", true, false), Map.empty, true),
       item)
  })
}

/**
 * For URLs of the for /foo/[ID]/suffix
 */
trait SuffixLoc {
  self: ItemRewriteLoc[_, _] =>
  
  val pathSuffix: List[String]
  override def link = {
    new Link[MyT](pathPrefix ++ pathSuffix)
  }

  override def finishPath(itemBox: => Box[MyS],
                          suffix: List[String]): Box[MyT] = {
    if (suffix == pathSuffix) wrapItem(itemBox) else Empty
  }
}
