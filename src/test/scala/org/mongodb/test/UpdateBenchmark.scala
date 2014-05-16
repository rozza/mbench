package org.mongodb.test

import org.mongodb.Document

import com.allanbank.mongodb.Durability
import com.allanbank.mongodb.bson.builder.BuilderFactory

class UpdateBenchmark extends Benchmark {

  performance of "Single Updates" in {
    measure method "3.0.x Driver" in {
      bench(updateCounts, "Sync") {
        updateCount =>
          val client = getClient
          try {
            val collection = getCollection(client)
            val query = new Document("_id", 1)
            val update = new Document("$inc", new Document("counter", 1))
            (0 until updateCount).map(_ => {
              collection.find(query).update(update)
            })
          } finally {
            client.close()
          }
      }
      bench(updateCounts, "Async") {
        updateCount =>
          val client = getAsyncClient
          try {
            val collection = getAsyncCollection(client)
            val query = new Document("_id", 1)
            val update = new Document("$inc", new Document("counter", 1))
            (0 until updateCount).map(_ => {
              collection.find(query).update(update)
            }) foreach (future => future.get)
          } finally {
            client.close()
          }
      }
    }

    measure method "mongodb-driver-async" in {
      bench(updateCounts, "Sync")  {
        updateCount =>
          val client = getAllanBankClient
          try {
            val collection = getAllanBankAsyncCollection(client)
            val qBuilder = BuilderFactory.start()
            qBuilder.add("_id", 1)
            val query = qBuilder.build()

            val uBuilder = BuilderFactory.start()
            uBuilder.push("$inc").addLong("counter", 1)
            val update = uBuilder.build()

            (0 until updateCount).map(_ => {
              collection.update(query, update, Durability.ACK)
            })
          } finally {
            client.close()
          }
      }

      bench(updateCounts, "Async") {
        updateCount =>
          val client = getAllanBankClient
          try {
            val collection = getAllanBankAsyncCollection(client)
            val qBuilder = BuilderFactory.start()
            qBuilder.add("_id", 1)
            val query = qBuilder.build()

            val uBuilder = BuilderFactory.start()
            uBuilder.push("$inc").addLong("counter", 1)
            val update = uBuilder.build()

            (0 until updateCount).map(_ => {
              collection.updateAsync(query, update, Durability.ACK)
            }) foreach (future => future.get)
          } finally {
            client.close()
          }
      }
    }
  }

}
