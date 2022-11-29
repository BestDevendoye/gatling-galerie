package APISACC

import scala.concurrent.duration._
import sys.process._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps

class MdC_APISequentiel extends Simulation {

  private val host: String = System.getProperty("urlCible", "https://api.cfzo4zpg87-44galerie1-d1-public.model-t.cc.commerce.ondemand.com/occ/v2")

  private val VersionAppli: String = System.getProperty("VersionApp", "Vxx.xx.xx")
  private val TpsMonteEnCharge: Int = System.getProperty("tpsMonte", "2").toInt
  private val DureeMax: Int = System.getProperty("dureeMax", "5").toInt + TpsMonteEnCharge

  private val avecApiProduct: Int = System.getProperty("avecApiProduct", "0").toInt
  private val lesApiProducts: Int = System.getProperty("avecApiProducts", avecApiProduct.toString).toInt
  private val lesApiProductId: Int = System.getProperty("avecApiProductId", avecApiProduct.toString).toInt
  private val lesApireferences: Int = System.getProperty("avecApireferences", avecApiProduct.toString).toInt
  private val lesApisearch: Int = System.getProperty("avecApsearch", avecApiProduct.toString).toInt
  private val lesApireviews: Int = System.getProperty("avecReviews", avecApiProduct.toString).toInt

  private val lesCatalogues: Int = System.getProperty("avecCatalogues", avecApiProduct.toString).toInt

  private val lesAPIPages: Int = System.getProperty("avecPages", avecApiProduct.toString).toInt
  private val lesApinComponents: Int = System.getProperty("avecComponents", avecApiProduct.toString).toInt

  private val lesSrore: Int = System.getProperty("avecStore", avecApiProduct.toString).toInt

  private val lesPageID: Int = System.getProperty("avecPageID", avecApiProduct.toString).toInt

  private val lesApinComponentsid : Int = System.getProperty("avecComponentsid",avecApiProduct.toString).toInt
  private val lesAicartlocality : Int = System.getProperty("aveccartlocality",avecApiProduct.toString).toInt
  private val lesAPIGetRECO : Int = System.getProperty("avecgetreco",avecApiProduct.toString).toInt


  private val LeCoeff: Int = System.getProperty("coeff", "1").toInt
  private val nbVu: Int = 1 * LeCoeff

  val httpProtocol =
    http.baseUrl(host)
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .acceptEncodingHeader("gzip, deflate")
      .acceptLanguageHeader("fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
      .userAgentHeader("TESTS-DE-PERF")
      .silentResources


  val ProductsId = scenario("ProductIds").exec(ObjectProductAPI.scnProductsId)
  val Productsreferences = scenario("References").exec(ObjectProductAPI.scnProductsreferences)
  val ProductSearch = scenario("Search").exec(ObjectProductAPI.scnProductSearch)
  val ProductReviews = scenario("REVIEWS").exec(ObjectProductAPI.scnProductReviews)
  val Catalogues = scenario("Catalogues").exec(ObjectCataloguetAPI.scnCatalogue)
  val ApiPages = scenario("API_PAGES").exec(ObjectPagesAPI.scnPages)
  val APIConponents = scenario("Components").exec(ObjectComponentsAPI.scnComponents)
  val APIStores = scenario("Store").exec(ObjectStoretAPI.scnStore)
  val apiPagesid = scenario("PAGEID").exec(ObjectPagesAPI.scnPAGEID)
  val APIConponentsid = scenario("COMPONMENTID").exec(ObjectComponentsAPI.scncomponementID)
  val APICartLocality = scenario("CART_LOACLITY").exec(ObjectCartlocalityAPI.scnCartlocality)
  val APIGetReco   = scenario("GET RECO").exec(ObjectGetRecoAPi.scnCartRECO)



    setUp(

      APIStores.inject(rampUsers(nbVu * lesSrore) during (TpsMonteEnCharge minutes)),
      APIConponents.inject(rampUsers(nbVu * lesApinComponents) during (TpsMonteEnCharge minutes)),
      ApiPages.inject(rampUsers(nbVu * lesAPIPages) during (TpsMonteEnCharge minutes)),
      Catalogues.inject(rampUsers(nbVu * lesCatalogues) during (TpsMonteEnCharge minutes)),
      ProductsId.inject(rampUsers(nbVu * lesApiProductId) during (TpsMonteEnCharge minutes)),
      Productsreferences.inject(rampUsers(nbVu * lesApireferences) during (TpsMonteEnCharge minutes)),
      ProductSearch.inject(rampUsers(nbVu * lesApisearch) during (TpsMonteEnCharge minutes)),
      ProductReviews.inject(rampUsers(nbVu * lesApireviews) during (TpsMonteEnCharge minutes)),
      apiPagesid.inject(rampUsers(nbVu * lesPageID) during (TpsMonteEnCharge minutes)),
      APICartLocality.inject(rampUsers(nbVu * lesAicartlocality) during (TpsMonteEnCharge minutes)),
      APIGetReco.inject(rampUsers(nbVu * lesAPIGetRECO) during (TpsMonteEnCharge minutes))


    ).protocols(httpProtocol).maxDuration(DureeMax minutes)



}

