package APISACC
import APISACC.ObjectProductAPI.{NbreIter, TempoMillisecond, groupBy, jddDataProductId, tpsPacingProducts}

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps

///////////////////////////////////////////////////
///	object : Product Pages	            ///
///////////////////////////////////////////////////

object ObjectPagesAPI {


  private val tpsPaceDefault: Int = System.getProperty("tpsPace", "1000").toInt
  private val tpsPacingProducts: Int = System.getProperty("tpsPaceProducts", tpsPaceDefault.toString).toInt


  private val TpsPause: Int = System.getProperty("tpsPause", "10").toInt
  private val TempoMillisecond: Int = System.getProperty("TempoMillisecond", "10").toInt

  //private val NbreIter : Int = System.getProperty("nbIter", "5000").toInt
  private val NbreIterDefault: Int = System.getProperty("nbIter", "10").toInt
  private val NbreIter: Int = System.getProperty("nbIterProduct", NbreIterDefault.toString).toInt
  private val FichierPath: String = System.getProperty("dataDir", "./src/test/resources/data/")

  private val FichierDataPageId: String = "pageid.csv"
  val jddDataPageId = csv( FichierPath + FichierDataPageId ).circular

  private val groupBy: String = System.getProperty("groupBy", "API_Pages")

  ///////////////////////////////////////////////////
  ///     scenario :      API_Pages             ///
  ///////////////////////////////////////////////////
  val scnPages = scenario("API_Pages")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Pages") {
      exec { session => session.set("LeGroup", "API_Page") }
    } {
      exec { session => session.set("LeGroup", "Page") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "Page") }


        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/cms/pages")
            .header("Content-Type", "application/json")
            .check(status.is(200))
            .check(regex("errors").notExists.name("Page_Erreur"))
          )

            //.pause ( TpsPause milliseconds)


            //////////////////////////////////////////////////////
            ///     suivant la présence des Erreurs : trace
            //////////////////////////////////////////////////////
            .doIf(session => session.attributes.contains("LesCodesErreurs")) {
              foreach("${LesCodesErreurs}", "UnCode") {
                exec { session => println("[PERF_MSG] Pour L_API " + session("L_API").as[String] + " , Erreur = " + session("UnCode").as[String]); session }
                  .pause(TempoMillisecond milliseconds)
              }
                .exec(session => session.remove("LesMessagesErreurs"))
            }

        } // Fin group("${LeGroup}")
    } // Fin forever



  ///////////////////////////////////////////////////
  ///     scenario :      PageID             ///
  ///////////////////////////////////////////////////

  val scnPAGEID = scenario("PAGEID")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Pages") {
      exec { session => session.set("LeGroup", "API_Pages") }
    } {
      exec { session => session.set("LeGroup", "API_Pages") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "PAGEID") }

        .feed(jddDataPageId)

        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/cms/pages/${pageid}")
            .header("Content-Type", "application/json")
            .check(status.is(200))
            .check(regex("errors").notExists.name("ProductSearch_Erreur"))
          )

            //.pause ( TpsPause milliseconds)


            //////////////////////////////////////////////////////
            ///     suivant la présence des Erreurs : trace
            //////////////////////////////////////////////////////
            .doIf(session => session.attributes.contains("LesCodesErreurs")) {
              foreach("${LesCodesErreurs}", "UnCode") {
                exec { session => println("[PERF_MSG] Pour L_API " + session("L_API").as[String] + " , Erreur = " + session("UnCode").as[String]); session }
                  .pause(TempoMillisecond milliseconds)
              }
                .exec(session => session.remove("LesMessagesErreurs"))
            }

        } // Fin group("${LeGroup}")
    } // Fin forever
}
