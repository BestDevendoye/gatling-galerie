package APISACC
import APISACC.ObjectCataloguetAPI.{NbreIter, TempoMillisecond, groupBy, tpsPacingProducts}

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps

///////////////////////////////////////////////////
///     object : Store           ///
///////////////////////////////////////////////////

object ObjectStoretAPI {
  private val tpsPaceDefault: Int = System.getProperty("tpsPace", "1000").toInt
  private val tpsPacingProducts: Int = System.getProperty("tpsPaceProducts", tpsPaceDefault.toString).toInt


  private val TpsPause: Int = System.getProperty("tpsPause", "10").toInt
  private val TempoMillisecond: Int = System.getProperty("TempoMillisecond", "10").toInt

  //private val NbreIter : Int = System.getProperty("nbIter", "5000").toInt
  private val NbreIterDefault: Int = System.getProperty("nbIter", "10").toInt
  private val NbreIter: Int = System.getProperty("nbIterProduct", NbreIterDefault.toString).toInt

  private val groupBy: String = System.getProperty("groupBy", "API_Store")


  ///////////////////////////////////////////////////
  ///     scenario :      Store             ///
  ///////////////////////////////////////////////////

  val scnStore = scenario("API_Store")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Store") {
      exec { session => session.set("LeGroup", "API_Store") }
    } {
      exec { session => session.set("LeGroup", "Store") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "Store") }


        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/stores/country/GB")
            .header("Content-Type", "application/json")
            .check(status.is(200))
            .check(regex("errors").notExists.name("Store_Erreur"))
          )

            //.pause ( TpsPause milliseconds)


            //////////////////////////////////////////////////////
            ///     suivant la pr??sence des Erreurs : trace
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
