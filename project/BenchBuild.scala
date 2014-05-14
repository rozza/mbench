import sbt._
import Keys._

object BenchBuild extends Build {

  import Dependencies._
  import Resolvers._

  val buildSettings = Seq(
    organization := "org.mongodb",
    version := "0.1-SNAPSHOT",
    scalaVersion := scalaCoreVersion,
    libraryDependencies ++= coreDependencies,
    resolvers := benchResolvers,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature" /*, "-Xlog-implicits", "-Yinfer-debug", "-Xprint:typer" */),
    scalacOptions in(Compile, doc) ++= Seq("-diagrams", "-implicits")
  )

  // Test configuration
  val testSettings = Seq(
    testFrameworks := Seq(new TestFramework("org.scalameter.ScalaMeterFramework")),
    logBuffered in Test := false
  )

  lazy val benchmarks = Project(
    id = "benchmarks",
    base = file(".")
  ).settings(buildSettings: _*)
    .settings(testSettings: _*)

}

object Dependencies {
  // Versions
  val scalaCoreVersion     = "2.11.0"
  val mongodbDriverVersion = "3.0.0-SNAPSHOT"
  val allenBankDriverVersion = "1.2.3"
  val scalaMeterVersion    = "0.5-SNAPSHOT"

  // Scala
  val scalaReflection    = "org.scala-lang" % "scala-reflect" % scalaCoreVersion
  val scalaCompiler      = "org.scala-lang" % "scala-compiler" % scalaCoreVersion

  // Libraries
  val mongodbDriver = "org.mongodb" % "mongodb-driver" % mongodbDriverVersion
  val mongodbAsyncDriver = "org.mongodb" % "async-driver" % mongodbDriverVersion
  val allenBankDriver = "com.allanbank" % "mongodb-async-driver" % allenBankDriverVersion

  // Testing Libraries
  val scalaMeter    = "com.github.axel22" %% "scalameter" % scalaMeterVersion

  // Projects
  val coreDependencies = Seq(scalaCompiler, scalaReflection, scalaMeter, mongodbDriver, mongodbAsyncDriver, allenBankDriver)
}

object Resolvers {

  // Repositories
  val sonatypeSnaps = "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatypeRels  = "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"
  val allenBankRels = "Allen Bank releases" at "http://www.allanbank.com/repo"

  val typeSafeSnaps = "TypeSafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots"
  val typeSafeRels  = "TypeSafe releases" at "http://repo.typesafe.com/typesafe/releases"

  val localMaven    = "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

  val benchResolvers = Seq(localMaven, sonatypeSnaps, sonatypeRels, typeSafeSnaps, typeSafeRels, allenBankRels)
}
