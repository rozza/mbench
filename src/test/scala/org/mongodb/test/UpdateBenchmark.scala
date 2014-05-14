package org.mongodb.test

import org.mongodb.Document

import com.allanbank.mongodb.Durability
import com.allanbank.mongodb.bson.builder.BuilderFactory

class UpdateBenchmark extends Benchmark {

  performance of "Single Updates" in {
    measure method "3.0.x Driver" in {
      bench(counts, "Sync") {
        updateCount =>
          val collection = getCollection()
          val query = new Document("_id", 1)
          val update = new Document("$inc", new Document("counter", 1))
          (0 until updateCount).map(_ => {
            collection.find(query).update(update)
          })
      }
      bench(counts, "Async") {
        updateCount =>
          val collection = getAsyncCollection()
          val query = new Document("_id", 1)
          val update = new Document("$inc", new Document("counter", 1))
          val futures = (0 until updateCount).map(_ => {
            collection.find(query).update(update)
          })
          for (future <- futures) future.get
      }
    }

    measure method "mongodb-driver-async" in {
      bench(counts, "Sync")  {
        updateCount =>
          val collection = getAllenBankAsyncCollection()
          val qBuilder = BuilderFactory.start()
          qBuilder.add("_id", 1)
          val query = qBuilder.build()

          val uBuilder = BuilderFactory.start()
          uBuilder.push("$inc").addLong("counter", 1)
          val update = uBuilder.build()

          (0 until updateCount).map(_ => {
            collection.update(query, update, Durability.ACK)
          })
      }

      bench(counts, "Async") {
        updateCount =>
          val collection = getAllenBankAsyncCollection()
          val qBuilder = BuilderFactory.start()
          qBuilder.add("_id", 1)
          val query = qBuilder.build()

          val uBuilder = BuilderFactory.start()
          uBuilder.push("$inc").addLong("counter", 1)
          val update = uBuilder.build()

          val futures = (0 until updateCount).map(_ => {
            collection.updateAsync(query, update, Durability.ACK)
          })
          for (future <- futures) future.get
      }
    }
  }

}
