import scala.collection.immutable.HashSet
import scala.collection.mutable

class WebPage(var url: String) {
   var pageUrl: String = url
   var parentUrls: mutable.HashSet[String] = new mutable.HashSet[String]()
   var childUrls: mutable.HashSet[String] = new mutable.HashSet[String]()

   def getPageUrl(): String = {
      return pageUrl
   }
   def addChildUrl(url: String): Unit = {
      childUrls.add(url)
   }
   def getChildUrls(): mutable.HashSet[String] = {
      return childUrls
   }

   def addParentUrl(url: String): Unit = {
      parentUrls.add(url)
   }
   def getParentUrls(): mutable.HashSet[String] = {
      return parentUrls
   }
}
