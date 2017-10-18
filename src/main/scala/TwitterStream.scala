import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

import org.apache.log4j.{Level, Logger}

import twitter4j.conf.ConfigurationBuilder
import twitter4j.auth.OAuthAuthorization
import twitter4j.Status
import org.apache.spark.streaming.twitter.TwitterUtils

import scala.util.matching.Regex



object TwitterStream {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[3]").setAppName("SlackStreaming")
    val ssc = new StreamingContext(conf, Seconds(5))

    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    val cb = new ConfigurationBuilder
    cb.setDebugEnabled(true).setOAuthConsumerKey("74wbDBcm0fjvg0ud1Tea6olaM")
      .setOAuthConsumerSecret("cShpWb9NFQAxHcT5UtoYGfoYrOetkXGDREoUuYsKMdv5X5Zxcl")
      .setOAuthAccessToken("148612813-NEbIraBKDVqyJNJsH5sdtT0d7eDmf3uICmCx6NDF")
      .setOAuthAccessTokenSecret("LwMDmUUxmnQ1natqAfYyCxARYZpgiIeAg3Dvh0fGSOOUn")


    val auth = new OAuthAuthorization(cb.build)
    val tweets = TwitterUtils.createStream(ssc, Some(auth))
    val englishTweets = tweets.filter(_.getLang() == "en")
    val text = englishTweets.map(_.getText)

    text.map(line => getHashTag(line)).filter(_ != "").print()



    //englishTweets.print()
    //if (args.length > 2) {
      //stream.saveAsTextFiles(args(2))
    //}
    //englishTweets .saveAsTextFiles("tweets", "json")
    ssc.start()
    ssc.awaitTermination()
  }

  def getHashTag(str: String):String  = {
    val pattern = new Regex("\\B#\\w\\w+")

    (pattern findAllIn str).mkString(" ").toLowerCase()
  }

}
