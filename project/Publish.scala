import sbt._, Keys._

object Publish {

  val coreSettings = Seq(
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <url>https://github.com/nstojiljkovic/play-swagger</url>
      <licenses>
        <license>
          <name>Apache License 2.0</name>
          <url>https://raw.githubusercontent.com/nstojiljkovic/play-swagger/master/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:nstojiljkovic/play-swagger.git</url>
        <connection>scm:git@github.com:nstojiljkovic/play-swagger.git</connection>
      </scm>
      <developers>
        <developer>
          <id>nstojiljkovic</id>
          <name>Nikola Stojiljković</name>
          <url>https://github.com/nstojiljkovic</url>
        </developer>
      </developers>),
    publishArtifact in Test := false)

  val sbtPluginSettings = Seq(
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := false)
}
