package org.mongodb.test

import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import org.mongodb.Document

class InsertBatchBenchmark extends Benchmark {

  performance of "Insert" in {
    measure method s"Synchronous insert with $count simple documents" in {
      bench(bulkInsert, "sync") {
        case(size, batches) =>
          val collection = getCollection()
          ( 0 until batches).map(_ => {
            val docs = (0 until size).map(i => new Document("filler", fillerString)).asJava
            collection.insert(docs)
          })
      }
    }

    measure method s"Asynchronous insert with $count simple documents" in {
      bench(bulkInsert, "async") {
        case(size, batches) =>
          val collection = getAsyncCollection()
          val futures = ( 0 until batches).map(_ => {
            val docs = (0 until size).map(i => new Document("filler", fillerString)).asJava
            Future(collection.insert(docs).get)
          })
          Await.result(Future.sequence(futures), Duration.Inf)
      }
    }
  }

}
