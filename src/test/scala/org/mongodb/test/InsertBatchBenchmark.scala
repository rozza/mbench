package org.mongodb.test

import scala.collection.JavaConverters._

import org.mongodb.Document

class InsertBatchBenchmark extends Benchmark {

  performance of "Bulk Inserts" in {
    measure method "3.0.x Driver" in {
      bench(bulkInsert, "Sync") {
        case(size, batches) =>
          val client = getClient
          try {
            val collection = getCollection(client)
            (0 until batches).map(_ => {
              val docs = (0 until size).map(i => new Document("filler", fillerString)).asJava
              collection.insert(docs)
            })
          } finally {
            client.close()
          }
      }

      bench(bulkInsert, "Async") {
        case(size, batches) =>
          val client = getAsyncClient
          try {
            val collection = getAsyncCollection(client)
            val futures = (0 until batches).map(_ => {
              val docs = (0 until size).map(i => new Document("filler", fillerString)).asJava
              collection.insert(docs)
            })
            for (future <- futures) future.get
          } finally {
            client.close()
          }
      }
    }
  }
}
