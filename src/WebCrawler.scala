import java.io.{BufferedWriter, File, FileWriter}
import java.util

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable
import scala.collection.mutable.LinkedHashSet


object WebCrawler {
   private var visitedUrls: LinkedHashSet[String] = LinkedHashSet[String]()
   private var newUrls: LinkedHashSet[String] = LinkedHashSet[String]()

   def addVisitedUrls(url: String): Boolean = {
      this.synchronized {
         return visitedUrls.add(url)
      }
   }

   def addNewUrls(url: String): Boolean = {
      this.synchronized {
         if (!visitedUrls.contains(url)) {
            return newUrls.add(url)
         }
         else {
            return false
         }
      }
   }

   def getNewUrl(): String = {
      this.synchronized {
         var newUrl: String = newUrls.last
         newUrls.remove(newUrl)
         return newUrl
      }
   }
}

class WebCrawler(var url: String, var nLevel: Int) extends Thread {
   var rootUrl: String = url
   var nestingLevel: Int = nLevel

   def getRootUrl(): String = {
      return this.rootUrl
   }
   def setRootUrl(newRoot: String): Unit = {
      this.rootUrl = newRoot
   }

   def crawlPage(url: String = rootUrl): Unit = {
      Thread.sleep(1200)
      var htmlPage: Document = Jsoup.connect(url).get()
      var linksOnPage: Elements = htmlPage.select("a[href]")

      var iterator: util.Iterator[Element] = linksOnPage.iterator()
      while (iterator.hasNext) {
         var childUrl: String = iterator.next().attr("abs:href")
         if (WebCrawler.addNewUrls(childUrl)) {
            println("New URL to visit added: " + childUrl)
         }
      }

      if (WebCrawler.addVisitedUrls(url)) {
         println("Visited URL: " + url)
         savePage(url, htmlPage)
      }

      while (WebCrawler.newUrls.nonEmpty) {
         new WebCrawler(WebCrawler.getNewUrl(), (nestingLevel - 1)).run()
      }
   }

   def savePage(url: String, htmlPage: Document): Unit = {
      val fileName: String = "StoredPages/" + "page_" + WebCrawler.visitedUrls.size + ".html"
      val file = new File(fileName)
      if (file.createNewFile())
      {
         println("Created file: " + file.getName)
      }
      else {
         println("File already exists: " + file.getName)
      }

      val fileWriter = new BufferedWriter(new FileWriter(file))
      fileWriter.write(htmlPage.toString)
      fileWriter.close()
   }

   override def run(): Unit = {
      println("Doing thread stuff, please come back later.")
      if (this.nestingLevel > 0) {
         this.crawlPage()
      }
      else {
         println("Nesting level 0 reached.")
      }
      join()
   }
}
