package APISACC
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps


///////////////////////////////////////////////////
///	object : Product Product		            ///
///////////////////////////////////////////////////
object ObjectProductAPI {


  private val tpsPaceDefault: Int = System.getProperty("tpsPace", "1000").toInt
  private val tpsPacingProducts: Int = System.getProperty("tpsPaceProducts", tpsPaceDefault.toString).toInt

  private val avecProduit: Boolean = System.getProperty("avecProduit", "false").toBoolean
  private val avecReferences: Boolean = System.getProperty("avecReferences", "false").toBoolean
  private val avecStock: Boolean = System.getProperty("avecStock", "false").toBoolean
  private val avecSearch: Boolean = System.getProperty("avecSearch", "false").toBoolean


  private val TpsPause: Int = System.getProperty("tpsPause", "10").toInt
  private val TempoMillisecond: Int = System.getProperty("TempoMillisecond", "10").toInt

  //private val NbreIter : Int = System.getProperty("nbIter", "5000").toInt
  private val NbreIterDefault: Int = System.getProperty("nbIter", "10").toInt
  private val NbreIter: Int = System.getProperty("nbIterProduct", NbreIterDefault.toString).toInt

  private val groupBy: String = System.getProperty("groupBy", "API_Product")

  private val FichierPath: String = System.getProperty("dataDir", "./src/test/resources/data/")

  private val FichierDataProductId: String = "JddApiProductId.csv"

  val jddDataProductId = csv( FichierPath + FichierDataProductId ).circular

  ///////////////////////////////////////////////////
  ///     scenario :      ProductID             ///
  ///////////////////////////////////////////////////

  val scnProductsId = scenario("ProductIds")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Product") {
      exec { session => session.set("LeGroup", "API_Product") }
    } {
      exec { session => session.set("LeGroup", "Product") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "ProductId") }

        .feed(jddDataProductId)

        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/products/${productcode}")
            .header("Content-Type", "application/json")
            .check(status.is(200))
            .check(regex("errors").notExists.name("ProductId_Erreur"))
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
  ///     scenario :      REFERENCES             ///
  ///////////////////////////////////////////////////


  val scnProductsreferences = scenario("References")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Product") {
      exec { session => session.set("LeGroup", "API_Product") }
    } {
      exec { session => session.set("LeGroup", "Product") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "References") }

        .feed(jddDataProductId)

        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/products/${productcode}/references")
            .header("Content-Type", "application/json")
            .check(status.is(200))
            .check(regex("errors").notExists.name("Productrefences_Erreur"))
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
  ///     scenario :      SEARCH             ///
  ///////////////////////////////////////////////////

  val scnProductSearch = scenario("Search")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Product") {
      exec { session => session.set("LeGroup", "API_Product") }
    } {
      exec { session => session.set("LeGroup", "Product") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "Search") }

        .feed(jddDataProductId)

        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/products/search")
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

  ///////////////////////////////////////////////////
  ///     scenario :      REVIEWS             ///
  ///////////////////////////////////////////////////


  val scnProductReviews = scenario("REVIEWS")

    .exec { session => session.set("detail", groupBy) }
    .doIfEqualsOrElse(session => session("detail").as[String], "API_Product") {
      exec { session => session.set("LeGroup", "API_Product") }
    } {
      exec { session => session.set("LeGroup", "Product") }
    }

    .repeat(NbreIter) { //.forever {
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)

        .pace(tpsPacingProducts milliseconds)

        .exec { session => session.set("L_API", "REVIEWS") }

        .feed(jddDataProductId)

        .group("${LeGroup}") {
          exec(http("${L_API}")
            .get("/gl-fr/products/${productcode}/reviews")
            .header("Content-Type", "application/json")
            .check(status.is(200))
            .check(regex("errors").notExists.name("Productreviews_Erreur"))
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
