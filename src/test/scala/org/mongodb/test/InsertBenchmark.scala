package org.mongodb.test

import org.mongodb.Document

import com.allanbank.mongodb.Durability
import com.allanbank.mongodb.bson.builder.BuilderFactory

class InsertBenchmark extends Benchmark {

  performance of "Single Inserts" in {
    measure method "3.0.x Driver" in {
      bench(counts, "Sync") {
        insertCount =>
          val collection = getCollection()
          (0 until insertCount).map(_ => {
            collection.insert(new Document("filler", fillerString))
          })
      }

      bench(counts, "Async") {
        insertCount =>
          val collection = getAsyncCollection()
          val futures = (0 until insertCount).map(_ => {
            collection.insert(new Document("filler", fillerString))
          })
          for (future <- futures) future.get
      }
    }

    measure method "mongodb-driver-async" in {
      bench(counts, "Sync")  {
        insertCount =>
          val collection = getAllenBankAsyncCollection()
          val document = BuilderFactory.start()
          document.add("filler", fillerString)
          (0 until insertCount).map(_ => {
            collection.insert(Durability.ACK, document.build())
          })
      }

      bench(counts, "Async") {
        insertCount =>
          val collection = getAllenBankAsyncCollection()
          val document = BuilderFactory.start()
          document.add("filler", fillerString)
          val futures = (0 until insertCount).map(_ => {
            collection.insertAsync(Durability.ACK, document.build())
          })
          for (future <- futures) future.get
      }

    }

  }

}
