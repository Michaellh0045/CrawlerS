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
   var illegalCharacters: Set[Char] = "<>:\"/\\|?*=".toSet
   var urls: LinkedHashSet[String] = LinkedHashSet[String](url)
   var visitedUrls: LinkedHashSet[String] = LinkedHashSet[String]()
   var storageDir: String = "StoredPages/" + rootUrl.filterNot(illegalCharacters).stripPrefix("https")

   def crawlUrl(url: String = rootUrl): Unit = {
      var retrievedUrls: LinkedHashSet[String] = LinkedHashSet[String]()
      Thread.sleep(1100)

      try {
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
      } catch {
         case e: Exception => println(e)
      }
   }

   def savePage(url: String, htmlPage: Document): Unit = {
      var fileName: String = "StoredPages/" + rootUrl.filterNot(illegalCharacters).stripPrefix("https") +
         "/" + url.filterNot(illegalCharacters).stripPrefix("https") + ".html"

      if (fileName.length > 255)
      {
         fileName = fileName.dropRight(fileName.size - 255)
      }
      try {
         val file = new File(fileName)
         if (file.createNewFile()) {
            println("Created file: " + file.getName)
         }
         else {
            println("File already exists: " + file.getName)
         }

         val fileWriter = new BufferedWriter(new FileWriter(file))
         fileWriter.write(htmlPage.toString)
         fileWriter.close()
      } catch {
         case e: Exception => println("File name cause exception: " + fileName)
      }
   }

   def makeStorageDir(): Unit = {
      var dir: File = new File(storageDir)
      if(!dir.exists()) {
         dir.mkdir()
      }
   }

   def isWithinNestingLevel(url: String): Boolean = {
      val res = url.stripPrefix("https://").count(p => equals("/")).<(nestingLevel)
      if (!res) {
//         println("URL(" + url + ") not within nesting level.")
      }
      res
   }

   def isChildOfRoot(url: String): Boolean = {
      val res = url.contains(rootUrl.stripPrefix("https://"))
      if (!res) {
//         println("URL(" + url + ") not child of root(" + rootUrl + ")")
      }
      res
   }

   override def run(): Unit = {
      makeStorageDir()
         while (this.urlIndex < urls.size) {
            crawlUrl(urls.toArray.array(urlIndex))
            urlIndex += 1
         }
   }
}
