package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import sample.model._

class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    Schemifier.schemify(true, Schemifier.infoF _, User)

    LiftRules.addToPackages("sample")

    def menu:List[Menu] = 
      List(Menu.i("Home") / "index" >> User.AddUserMenusAfter, 
           Menu(sample.snippet.VenuePage), 
           Menu(sample.snippet.VenuePagePhotos),
           Menu(sample.snippet.CheckinPage))

    def sitemap = SiteMap(menu:_*)

    def sitemapMutators = User.sitemapMutator

    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

    S.addAround(DB.buildLoanWrapper)

    if(User.findAll.length==0){
      User.create.
           firstName("Foo").lastName("Bar").
           email("foo@example.com").password("*****").saveMe
    }
  }
}
