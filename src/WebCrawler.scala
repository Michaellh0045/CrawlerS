import java.util

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable
import scala.collection.mutable.HashSet

class WebCrawler(var url: String) extends Thread {
   var rootUrl: String = url
   var urlLinks: HashSet[String] = new mutable.HashSet[String]()

   def getCrawlerUrl(): String = {
      return this.rootUrl
   }
   def setCrawlUrl(url : String): Unit = {
      this.rootUrl = url
   }

   def getRootUrl(): String = {
      return this.rootUrl
   }
   def setRootUrl(newRoot: String): Unit = {
      this.rootUrl = newRoot
   }

   def getPageLinks(url: String): Unit = {
      var htmlPage: Document = Jsoup.connect(url).get()
      var linksOnPage: Elements = htmlPage.select("a[href]")
      var iterator: util.Iterator[Element] = linksOnPage.iterator()

      // If the url has not been visited then add it to the urlLinks container
      if (!urlLinks.contains(url)) {
         urlLinks.add(url)

         while (iterator.hasNext) {
            //         getPageLinks(iterator.next().attr("abs:href"))
            println(iterator.next().toString)
         }

      }
   }

   override def run(): Unit = {
      println("Doing thread stuff, please come back later.")
   }
}
