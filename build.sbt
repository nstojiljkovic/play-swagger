name := "Play Swagger"

val scala212Version = "2.12.8"
val scala213Version = "2.13.0"

scalaVersion := scala213Version

organization in ThisBuild := "com.github.nstojiljkovic"

resolvers += Resolver.bintrayRepo("scalaz", "releases")

lazy val root =
  (project in file("."))
    .settings(Publish.commonSettings ++ Publish.noPublish)
    .settings(sourcesInBase := false)
    .aggregate(playSwagger, playSwaggerJackson, sbtPlaySwagger)

lazy val playSwagger = project.in(file("core"))
  .settings(Publish.commonSettings ++ Publish.coreSettings ++ Format.settings ++ Testing.settings)
  .settings(
    name := "play-swagger",
    crossScalaVersions := Seq(scala212Version, scala213Version),
    libraryDependencies ++= Dependencies.playTest ++
      Dependencies.playRoutesCompiler ++
      Dependencies.playJson ++
      Dependencies.jackson ++
      Dependencies.test ++
      Dependencies.yaml,
    scalaVersion := scala213Version
  )

lazy val playSwaggerJackson = project.in(file("jackson"))
  .settings(Publish.commonSettings ++ Publish.coreSettings ++ Format.settings ++ Testing.settings)
  .settings(
    name := "play-swagger-jackson",
    crossScalaVersions := Seq(scala212Version, scala213Version),
    libraryDependencies ++= Dependencies.playTest ++
      Dependencies.playRoutesCompiler ++
      Dependencies.playJson ++
      Dependencies.jackson ++
      Dependencies.javaCompat ++
      Dependencies.javaTest,
    scalaVersion := scala213Version
  ).dependsOn(playSwagger)

lazy val sbtPlaySwagger = project.in(file("sbtPlugin"))
  .settings(Publish.commonSettings ++ Publish.sbtPluginSettings ++ Format.settings)
  .settings(
    addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.24" % Provided),
    addSbtPlugin("com.typesafe.sbt" %% "sbt-web" % "1.4.4" % Provided))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(SbtPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.github.nstojiljkovic.playSwagger",
    name := "sbt-play-swagger",
    crossScalaVersions := Seq(scala212Version),
    description := "sbt plugin for play swagger spec generation",
    sbtPlugin := true,
    scalaVersion := scala212Version,
    scripted := scripted.dependsOn(publishLocal in playSwagger).evaluated,

    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )

scalacOptions in ThisBuild ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xlint", // Enable recommended additional warnings.
)
