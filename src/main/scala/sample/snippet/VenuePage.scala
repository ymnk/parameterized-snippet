package sample.snippet 

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.sitemap._
import Helpers._

/* I'm too lazy to define the following model. ;-( */
case class Venue(id: String){
  val uri: String = id
}
object Venue {
  def find(id: String): Box[Venue] = Full(Venue(id))
}

case class VenuePageData(venue: Venue) extends PageData

object VenuePage extends ItemRewriteLoc[Venue, VenuePageData] {
  override val name = "venue"
  override val pathPrefix = "venue" :: Nil
  override def getItem(id: String) = Venue.find(id)
  override def wrapItem(venueBox: Box[Venue]) = venueBox.map(VenuePageData(_))
  override def canonicalUrl(data: VenuePageData) = {
    //Full(data.venue.uri) 
    Full((pathPrefix:::List(data.venue.uri)).mkString("/","/",""))
  }
}

class VenuePage(data: VenuePageData) {
  def snippet1 = 
    ".snippet" #> <span>snippet1: {data}, hashCode: {data.hashCode}</span>  
  def snippet2 = 
    ".snippet" #> <span>snippet2: {data}, hashCode: {data.hashCode}</span>  
  def snippet3 = 
    ".snippet" #> <span>snippet3: {data}, hashCode: {data.hashCode}</span>  
}

abstract class AbstractVenuePageLoc[Data <: PageData]
         extends ItemRewriteLoc[Venue, Data]

object VenuePagePhotos 
    extends AbstractVenuePageLoc[VenuePageData]
    with SuffixLoc {
  override val name = "venue_photos"
  override val pathPrefix = "venue" :: Nil
  override val pathSuffix = "photos" :: Nil
  override def getItem(id: String) = Venue.find(id)
  override def wrapItem(venueBox: Box[Venue]) = venueBox.map(VenuePageData(_))
}
class VenuePagePhoto(data: VenuePageData) {
  def snippet1 = 
    ".snippet" #> <span>snippet1: {data}, hashCode: {data.hashCode}</span>  
  def snippet2 = 
    ".snippet" #> <span>snippet2: {data}, hashCode: {data.hashCode}</span>  
  def snippet3 = 
    ".snippet" #> <span>snippet3: {data}, hashCode: {data.hashCode}</span>  
}
