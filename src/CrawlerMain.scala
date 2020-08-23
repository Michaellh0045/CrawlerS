import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object CrawlerMain {
   def main(args: Array[String]): Unit = {
      val crawler = new WebCrawler("https://scrapethissite.com/pages/")

      crawler.getPageLinks(crawler.url)
   }
}
