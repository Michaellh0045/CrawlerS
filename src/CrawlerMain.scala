import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

object CrawlerMain {
   /*
   ** args(0): csv file of website URLs to crawl
   ** args(1): Nesting level
   */
   def main(args: Array[String]): Unit = {
      var nestingLevel: Int = 1
      val urlList: ListBuffer[String] = new ListBuffer[String]()

      // If a CSV has been provided, populate a list with the values
      if (args.length > 0) {
         val bufferedSource = Source.fromFile(args(0))
         bufferedSource.getLines().mkString.split(",").foreach(str => urlList.addOne(str))
         bufferedSource.close()

         // If a nesting level has been provided, store it
         if (args.length > 1) {
            nestingLevel = args(1).toInt
         }
      }
      else {
         // Note: The website limits requests to a maximum of 1 per second.
         urlList.addOne("https://scrapethissite.com/")
      }

      // Create a crawler for each URL provided
      val crawlerList: ListBuffer[WebCrawler] = new mutable.ListBuffer
      for(url <- urlList) {
         crawlerList.addOne(new WebCrawler(url, nestingLevel))
         crawlerList.last.start()
      }
      crawlerList.foreach(thread => thread.join())
   }
}
