name := "Play Swagger"

scalaVersion := "2.12.8"

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
    libraryDependencies ++= Dependencies.playTest ++
      Dependencies.playRoutesCompiler ++
      Dependencies.playJson ++
      Dependencies.jackson ++
      Dependencies.test ++
      Dependencies.yaml,
    scalaVersion := "2.12.8"
  )

lazy val playSwaggerJackson = project.in(file("jackson"))
  .settings(Publish.commonSettings ++ Publish.coreSettings ++ Format.settings ++ Testing.settings)
  .settings(
    name := "play-swagger-jackson",
    libraryDependencies ++= Dependencies.playTest ++
      Dependencies.playRoutesCompiler ++
      Dependencies.playJson ++
      Dependencies.jackson ++
      Dependencies.javaCompat ++
      Dependencies.javaTest,
    scalaVersion := "2.12.8"
  ).dependsOn(playSwagger)

lazy val sbtPlaySwagger = project.in(file("sbtPlugin"))
  .settings(Publish.commonSettings ++ Publish.sbtPluginSettings ++ Format.settings)
  .settings(
    addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.17" % Provided),
    addSbtPlugin("com.typesafe.sbt" %% "sbt-web" % "1.4.4" % Provided))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.github.nstojiljkovic.playSwagger",
    name := "sbt-play-swagger",
    description := "sbt plugin for play swagger spec generation",
    sbtPlugin := true,
    scalaVersion := "2.12.8",
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
