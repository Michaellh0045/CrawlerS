import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object CrawlerMain {
   /*
   ** args(0): csv file of website URLs to crawl
   ** args(1): Nesting level
   */
   def main(args: Array[String]): Unit = {
      var urlList: ListBuffer[String] = new ListBuffer[String]
      var nestingLevel: Int = 3

      // If a CSV has been provided, populate a list with the values
      if (args.length > 0) {
         urlList = ListBuffer.from(args(0).split(",").toList)

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
      var crawlerList: ListBuffer[WebCrawler] = new mutable.ListBuffer
      for(url <- urlList) {
         crawlerList.addOne(new WebCrawler(url, nestingLevel))
         crawlerList.last.run()
      }

//      val testClass: ClassName = new ClassName()
//      testClass.run()
   }
}
