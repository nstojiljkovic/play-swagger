import sbt._
import Keys._
import sbtrelease.ReleasePlugin.autoImport.{ ReleaseStep, releaseProcess, releaseStepCommand, releaseVersionFile }
import sbtrelease.ReleaseStateTransformations._

object Publish {

  val commonSettings = Seq(
    releaseVersionFile := baseDirectory.value / "version.sbt",
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommand("publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("sonatypeReleaseAll"),
      pushChanges),
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

  val coreSettings = commonSettings
  val sbtPluginSettings = commonSettings
  val noPublish = Seq(
    publishArtifact := false,
    publishMavenStyle := false)
}
