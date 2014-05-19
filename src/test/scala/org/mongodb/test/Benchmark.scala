package org.mongodb.test

import java.util.logging.{Level, Logger}

import scala.util.Properties

import org.mongodb._

import com.allanbank.mongodb.{MongoClientConfiguration, MongoFactory}
import org.scalameter.{Reporter, Executor, Gen}
import org.scalameter.Executor.Measurer.Default
import org.scalameter.api.{PerformanceTest, exec}
import org.scalameter.reporting.{DsvReporter, HtmlReporter, RegressionReporter}
import org.mongodb.async
import com.allanbank.mongodb

trait Benchmark extends PerformanceTest.OnlineRegressionReport {

  override def measurer: Default = new Executor.Measurer.Default with Executor.Measurer.PeriodicReinstantiation
                                                                 with Executor.Measurer.OutlierElimination
                                                                 with Executor.Measurer.RelativeNoise

  override def reporter: Reporter = Reporter.Composite(
    new RegressionReporter(tester, historian),
    new DsvReporter(','),
    HtmlReporter(!online)
  )

  private val DEFAULT_URI: String = "mongodb://localhost:27017"
  private val MONGODB_URI_SYSTEM_PROPERTY_NAME: String = "org.mongodb.test.uri"

  val mongoClientURI = Properties.propOrElse(MONGODB_URI_SYSTEM_PROPERTY_NAME, DEFAULT_URI)
  val databaseName = "mongo-perf"
  def collectionName = this.getClass.getSimpleName

  def getClient = MongoClients.create(new MongoClientURI(mongoClientURI))
  def getDatabase(client: MongoClient) = client.getDatabase(databaseName)
  def getCollection(client: MongoClient) = getDatabase(client).getCollection(collectionName)

  def getAsyncClient = {
    val uri = new MongoClientURI(mongoClientURI)
    async.MongoClients.create(uri, uri.getOptions)
  }
  def getAsyncCollection(client: async.MongoClient) =
   client.getDatabase(databaseName).getCollection(collectionName)

  def getAllanBankClient: mongodb.MongoClient = {
    val config = new MongoClientConfiguration(mongoClientURI)
    MongoFactory.createClient(config)
  }

  def getAllanBankAsyncCollection(client: mongodb.MongoClient) =
    client.getDatabase(databaseName).getCollection(collectionName)

  val count = 5000
  val fillerString = "*" * 400
  val sizes = Gen.enumeration("Batch Sizes")(1, 10, 100, 1000, 5000)
  val loops: Gen[Int] = for {
    size <- sizes
  } yield {
    count / size
  }
  val bulkInsert = Gen.tupled(sizes, loops)
  val insertCounts = Gen.exponential("Operations")(100, 128000, 2)
  val updateCounts = Gen.exponential("Operations")(100, 128000, 2)


  def bench[T](gen: Gen[T], name: String)(block: T => Any) {
    using(gen) config(
      exec.benchRuns -> 5,
      exec.independentSamples -> 2,
      exec.minWarmupRuns -> 2,
      exec.maxWarmupRuns -> 4
      ) beforeTests {
      // Turn off org.mongodb's noisy connection INFO logging - only works with the JULLogger
      Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.WARNING)
      Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.WARNING)
      val client = getClient
      val db = getDatabase(client)
      db.tools.drop()
      db.tools.createCollection(collectionName)
      client.close()
    } afterTests {
      val client = getClient
      getDatabase(client).tools.drop()
      client.close()
    } setUp {_ => {
      val client = getClient
      getCollection(client).tools().drop()
      getCollection(client).insert(new Document("_id", 1))
      client.close()
    }} curve name in block
  }

}
