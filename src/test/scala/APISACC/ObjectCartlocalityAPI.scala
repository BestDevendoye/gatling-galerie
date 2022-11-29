package APISACC
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps

///////////////////////////////////////////////////
///     object : GET CART LOCALITY           ///
///////////////////////////////////////////////////
object ObjectCartlocalityAPI {

  private val tpsPaceDefault: Int = System.getProperty("tpsPace", "1000").toInt
  private val tpsPacingProducts: Int = System.getProperty("tpsPaceProducts", tpsPaceDefault.toString).toInt


  private val TpsPause: Int = System.getProperty("tpsPause", "10").toInt
  private val TempoMillisecond: Int = System.getProperty("TempoMillisecond", "10").toInt

  //private val NbreIter : Int = System.getProperty("nbIter", "5000").toInt
  private val NbreIterDefault: Int = System.getProperty("nbIter", "10").toInt
  private val NbreIter: Int = System.getProperty("nbIterProduct", NbreIterDefault.toString).toInt

  private val groupBy: String = System.getProperty("groupBy", "API_CART_LOCALITY")

  ///////////////////////////////////////////////////
  ///     scenario :      GET CART LOCALITY             ///
  ///////////////////////////////////////////////////

  val scnCartlocality = scenario("API_scnCartlocality")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_CART_LOCALITY") {
      exec { session => session.set("LeGroup", "API_CART_LOCALITY") }
    } {
      exec { session => session.set("LeGroup", "CART_LOACLITY") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "CART_LOACLITY") }


        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/users/anonymous/carts/778723b9-5ba2-474a-ae36-7b7a79b500b1/loyalty?lang=fr&curr=EUR")
            .header("Content-Type", "application/json")
            .header("Cookie","ROUTE=.api-79f88f6fb6-bn9t4")
            .check(status.is(200))
            .check(regex("errors").notExists.name("catalogs_Erreur"))
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
