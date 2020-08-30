import java.io.{BufferedWriter, File, FileWriter}
import java.util

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable

class WebCrawler(var url: String, var nLevel: Int) extends Thread {
   var urlIndex: Int = 0
   var rootUrl: String = url
   var nestingLevel: Int = nLevel
   var illegalCharacters: Set[Char] = "<>:\"/\\|?*=".toSet
   var urls: mutable.LinkedHashSet[String] = mutable.LinkedHashSet[String](url)
   var visitedUrls: mutable.LinkedHashSet[String] = mutable.LinkedHashSet[String]()
   var storageDir: String = "StoredPages/" + rootUrl.filterNot(illegalCharacters).stripPrefix("https")

   def crawlUrl(url: String = rootUrl): Unit = {
      // Note: Thread sleep is here so that sites don't perceive a DDOS attempt.
      Thread.sleep(1100)

      try {
         val htmlPage: Document = Jsoup.connect(url).get()
         val linksOnPage: Elements = htmlPage.select("a[href]")

         val iterator: util.Iterator[Element] = linksOnPage.iterator()
         while (iterator.hasNext) {
            val childUrl: String = iterator.next().attr("abs:href")
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
         fileName = fileName.dropRight(fileName.length - 255)
      }
      try {
         val file = new File(fileName)
         if (file.createNewFile()) {
            println("Created file for url: " + url)
         }
         else {
            println("File already exists: " + file.getName)
         }

         val fileWriter = new BufferedWriter(new FileWriter(file))
         fileWriter.write(htmlPage.toString)
         fileWriter.close()
      } catch {
         case e: Exception => println("File name cause exception: " + fileName)
            println("Exception: " + e.toString)
      }
   }

   def makeStorageDir(): Unit = {
      val dir: File = new File(storageDir)
      if(!dir.exists()) {
         dir.mkdir()
      }
   }

   def isWithinNestingLevel(url: String): Boolean = {
      val slashCount = url.stripPrefix("https://").count(_ == '/')
      if (slashCount <= nestingLevel) {
         println("URL(" + url + ") is within nesting level. [" + slashCount + "," + nestingLevel + "]")
         true
      } else {
         println("URL(" + url + ") not within nesting level. [" + slashCount + "," + nestingLevel + "]")
         false
      }
   }

   def isChildOfRoot(url: String): Boolean = {
      url.contains(rootUrl.stripPrefix("https://"))
   }

   override def run(): Unit = {
      makeStorageDir()
         while (this.urlIndex < urls.size) {
            crawlUrl(urls.toArray.array(urlIndex))
            urlIndex += 1
         }
   }
}
