import sbt._

object Dependencies {

  object Versions {
    val play = "2.7.3"
    val playJson = "2.7.4"
    val specs2 = "4.6.0"
  }

  val playTest = Seq(
    "com.typesafe.play" %% "play-test" % Versions.play % Test)

  val playRoutesCompiler = Seq(
    "com.typesafe.play" %% "routes-compiler" % Versions.play)

  val playJson = Seq(
    "com.typesafe.play" %% "play-json" % Versions.playJson % "provided")

  val yaml = Seq(
    "org.yaml" % "snakeyaml" % "1.24")

  val test = Seq(
    "org.specs2" %% "specs2-core" % Versions.specs2 % "test",
    "org.specs2" %% "specs2-mock" % Versions.specs2 % "test")

  val javaTest = Seq(
    "junit" % "junit" % "4.12" % "test",
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "org.skyscreamer" % "jsonassert" % "1.5.0" % "test")

  val javaCompat = Seq(
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0")

  val jackson = Seq(
    "com.fasterxml.jackson.module" % "jackson-module-jsonSchema" % "2.9.9")
}
