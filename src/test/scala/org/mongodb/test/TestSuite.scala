package org.mongodb.test

import org.scalameter.Executor
import org.scalameter.Executor.Measurer.Default
import org.scalameter.PerformanceTest.{OnlineRegressionReport, HTMLReport}
import org.scalameter.reporting.RegressionReporter

class TestSuite extends OnlineRegressionReport {

  override def tester: RegressionReporter.Tester = RegressionReporter.Tester.Accepter()
  override def measurer: Default = new Executor.Measurer.Default

  include[InsertBenchmark]
  include[UpdateBenchmark]


}
