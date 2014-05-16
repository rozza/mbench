package org.mongodb.test

import org.mongodb.Document

import com.allanbank.mongodb.Durability
import com.allanbank.mongodb.bson.builder.BuilderFactory

class InsertBenchmark extends Benchmark {

  performance of "Single Inserts" in {
    measure method "3.0.x Driver" in {
      bench(insertCounts, "Sync") {
        insertCount =>
          val client = getClient
          try {
            val collection = getCollection(client)
            (0 until insertCount).map(_ => {
              collection.insert(new Document("filler", fillerString))
            })
          } finally {
            client.close()
          }

      }

      bench(insertCounts, "Async") {
        insertCount =>
          val client = getAsyncClient
          try {
            val collection = getAsyncCollection(client)
            (0 until insertCount).map(_ => {
              collection.insert(new Document("filler", fillerString))
            }) foreach (future => future.get)
          } finally {
            client.close()
          }
      }
    }

    measure method "mongodb-driver-async" in {
      bench(insertCounts, "Sync")  {
        insertCount =>
          val client = getAllanBankClient
          try {
            val collection = getAllanBankAsyncCollection(client)
            val document = BuilderFactory.start()
            document.add("filler", fillerString)
            (0 until insertCount).map(_ => {
              collection.insert(Durability.ACK, document.build())
            })
          } finally {
            client.close()
          }
      }

      bench(insertCounts, "Async") {
        insertCount =>
          val client = getAllanBankClient
          try {
            val collection = getAllanBankAsyncCollection(client)
            val document = BuilderFactory.start()
            document.add("filler", fillerString)
            (0 until insertCount).map(_ => {
              collection.insertAsync(Durability.ACK, document.build())
            }) foreach (future => future.get)
          } finally {
            client.close()
          }
      }

    }

  }

}
