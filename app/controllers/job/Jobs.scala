package controllers.job

/**
 * Created by vinaysaini on 10/7/15.
 */

import play.api.libs.json.{Json, JsValue}
import IndeedApi._
import play.api.libs.ws._
import play.api.Play.current

import scala.util.parsing.json.JSONObject
import scala.util.{Failure, Success}
import scala.xml.{Node, NodeSeq}

class Jobs {

    def getJobList = {
      val feedId = "5955"
      val sourceId = "12190"
      val backend_script: JsValue = Json.parse(
        """
          |{
          |  "access_method": "indeed_api",
          |  "indeed_api": {
          |    "query": [
          |      "feedid:5955",
          |      "sourceid:12190"
          |    ],
          |    "countries": [],
          |    "max_jobs": 10,
          |    "source_tracking_token": "&tSource=a169000000090xAAAQ"
          |  }
          |}
        """.stripMargin)

      val query = "feedId:"+feedId+"sourceId:"+sourceId
      var countries = Array[String]()
      if(countries.isEmpty) countries = INDEED_API_DEFAULT_COUNTRIES  else countries.intersect(INDEED_API_DEFAULT_COUNTRIES)

      // Temporary overwriting - remove this line after testing
      countries = Array("us")
      val source_tracking_token = "&tSource=a169000000090xAAAQ"

      val jobs_to_fetch = 100
      val indeed_api_job_limit = 1100
      val total_jobs_extracted = 0
      val total_jobs_in_query = 0
      val max_jobs = 10
      val max_jobs_fetched = false
      val indeed_api_job_limit_reached = false

      var output: JsValue = Json.obj("status" -> "success")
      val indeed_api_publisher_id = INDEED_API_PUBLISHER_ID

      for(co <- countries) {
        var starting_job_index = 0
  //      while (!max_jobs_fetched && !indeed_api_job_limit_reached) {
          val (totaljobs, jobs) = get_job_info(s"http://api.indeed.com/ads/apisearch?publisher=$indeed_api_publisher_id&q=$query&l=&sort=date&start=$starting_job_index&limit=$jobs_to_fetch&co=$co&v=2&filter=0", indeed_api_publisher_id, co, source_tracking_token)
 //       }
      }


      println(query)

    }

    def get_job_info(apiUrl: String, indeed_api_publisher_id: String, co: String, source_tracking_token: String) = {
      type xmlMap = scala.collection.mutable.Map[String, Any]
      val sourceIdPattern = ".*sourceid:(?<sourceid>\\d+).*".r
      val sourceId = apiUrl match {
        case sourceIdPattern(sourceId) => sourceId
        case _ => ""
      }
      var jobs = List[JSONObject]()

      import scala.concurrent.ExecutionContext.Implicits.global
      val jobListXml = WS.url(apiUrl).get().map( response => response.xml )
      jobListXml.onComplete( {
        case Success(jobList) => {
          val jobsFromList = scala.collection.mutable.Map[String, Node]()
          val jobsFromInfo = scala.collection.mutable.Map[String, Node]()

          val jobListDetails: NodeSeq = jobList \\ "result"
          for(jobListDetail <- jobListDetails) {
            if((jobListDetail \\ "expired").text != "true") {
              val jobKey: String = (jobListDetail \\ "jobkey").text
              jobsFromList += (jobKey -> jobListDetail)
            }
          }
          val jobkeys = jobsFromList.keySet.mkString(",")
          val jobInfoUrl = s"http://www.indeed.com/ads/apigetjobs?jobkeys=$jobkeys&publisher=$indeed_api_publisher_id&v=latest&fs=1&descType=formatted"
          val jobsInfoXml = WS.url(jobInfoUrl).get().map( response => response.xml )
          jobsInfoXml.onComplete({
            case Success(jobsInfo) => {
              val jobInfoDetails: NodeSeq = jobsInfo \\ "result"
              val ignoredAttributes = Array("formattedLocation", "source", "onmousedown", "sponsored", "expired", "indeedApply", "formattedLocationFull", "formattedRelativeTime", "mobileThirdPartyApplyable", "iosAppFileInjectable", "mobileReformattable", "requiresDesktopSpoof", "mobileThirdPartyApplyUrlHack", "indeedApplyVisibilityLevels", "actualFormattingAlgo","url","locale", "backend_locale",
              "jobTypesCanonical","jobTypes", "#PCDATA", "bidOptimized", "snippet")
              for (jobInfoDetail <- jobInfoDetails) {
                if((jobInfoDetail \\ "expired").text != "true") {
                  val jobKey: String = (jobInfoDetail \\ "jobkey").text
                  jobsFromInfo += (jobKey -> jobInfoDetail)
                }
              }
              for(key <- jobsFromList.keySet) {
                val jobFromList = jobsFromList.get(key)
                val jobFromInfo = jobsFromInfo.get(key)
                if(jobFromInfo.isDefined) {
                  val jobMap: xmlMap = (fromXml(jobFromList.get)("result").asInstanceOf[xmlMap]).++(fromXml(jobFromInfo.get)("result").asInstanceOf[xmlMap])
                  for(k <- ignoredAttributes) jobMap -= k
                  val jobObject = scala.util.parsing.json.JSONObject(jobMap.toMap)
                  jobs = jobs.+:(jobObject)
                }
              }
              val output = Map("status" -> "success", "data" -> jobs)
            //  println(output)
            }
            case Failure(e) => println(e)
          })
        }
        case Failure(e) => println("exception")
        }
      )
      println(apiUrl)
      ("", "")
    }

    def fromXml(xml: Node): scala.collection.mutable.Map[String, Any] = {
      if(xml.child.size == 1 && xml.child(0).child.isEmpty) {
        return scala.collection.mutable.Map(xml.label -> xml.text)
      } else {
        val children = xml.child
        val childrenMap = scala.collection.mutable.Map[String, Any]()
        for(childNode <- children) {
            childrenMap ++= fromXml(childNode)
        }
        return scala.collection.mutable.Map(xml.label -> childrenMap)
      }
    }

}
