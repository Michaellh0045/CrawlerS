import java.util

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable.HashSet

class WebCrawler(var url: String) {
   var crawlUrl: String = url
   val urlLinks: HashSet[String] = HashSet.empty[String]

   def setCrawlUrl(url : String): Unit = {
      this.crawlUrl = url
   }

   def getPageLinks(url: String): Unit = {
      var htmlPage: Document = Jsoup.connect(url).get()
      var linksOnPage: Elements = htmlPage.select("a[href]")
      var iterator: util.Iterator[Element] = linksOnPage.iterator()

      while (iterator.hasNext) {
//         getPageLinks(iterator.next().attr("abs:href"))
         printf(iterator.next().toString)
      }

   }
}
