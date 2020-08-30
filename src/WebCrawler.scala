import java.io.{BufferedWriter, File, FileWriter}
import java.util

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable.LinkedHashSet

class WebCrawler(var url: String, var nLevel: Int) extends Thread {
   var urlIndex: Int = 0
   var rootUrl: String = url
   var nestingLevel: Int = nLevel
   var urls: LinkedHashSet[String] = LinkedHashSet[String](url)
   var visitedUrls: LinkedHashSet[String] = LinkedHashSet[String]()

   def crawlUrl(url: String = rootUrl): Unit = {
      var retrievedUrls: LinkedHashSet[String] = LinkedHashSet[String]()
      Thread.sleep(1100)
      var htmlPage: Document = Jsoup.connect(url).get()
      var linksOnPage: Elements = htmlPage.select("a[href]")

      var iterator: util.Iterator[Element] = linksOnPage.iterator()
      while (iterator.hasNext) {
         var childUrl: String = iterator.next().attr("abs:href")
         if (isChildOfRoot(childUrl) && isWithinNestingLevel(childUrl) && this.urls.add(childUrl)) {
            println("New URL to visit added: " + childUrl)
         }
      }

      savePage(url, htmlPage)
   }

   def savePage(url: String, htmlPage: Document): Unit = {
      var illegalCharacters: Set[Char] = "<>:\"/\\|?*".toSet
      var fileName: String = "StoredPages/" + rootUrl.filterNot(illegalCharacters) +
         url.filterNot(illegalCharacters).stripPrefix("https") + ".html"

      var dirName: String = "StoredPages/" + rootUrl.filterNot(illegalCharacters)
      var dir: File = new File(dirName)
      if(!dir.exists()) {
         dir.mkdir()
      }

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

   def makeStorageDir(): Unit = {
      var dirName: String = "StoredPages/" + rootUrl
   }

   def isWithinNestingLevel(url: String): Boolean = {
      url.stripPrefix("https://").count(p => equals("/")).<(nestingLevel)
   }

   def isChildOfRoot(url: String): Boolean = {
      url.contains(rootUrl)
   }

   override def run(): Unit = {
         while (this.urlIndex < urls.size) {
            crawlUrl(urls.toArray.array(urlIndex))
            urlIndex += 1
         }
   }
}
