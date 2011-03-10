package sample.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import Helpers._

import sample.lib._

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date]
  def howdy = "#time *" #> date.map(_.toString)
}
