val scala211Version            = "2.11.12"
val scala212Version            = "2.12.10"
val scala213Version            = "2.13.1"
val akka26Version              = "2.6.10"
val akka25Version              = "2.5.32"
val testcontainersScalaVersion = "0.38.4"

def crossScalacOptions(scalaVersion: String): Seq[String] =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2L, scalaMajor)) if scalaMajor >= 12 =>
      Seq.empty
    case Some((2L, scalaMajor)) if scalaMajor <= 11 =>
      Seq("-Yinline-warnings")
  }

lazy val deploySettings = Seq(
  sonatypeProfileName := "com.github.j5ik2o",
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := {
    <url>https://github.com/j5ik2o/akka-persistence-s3</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:j5ik2o/akka-persistence-s3.git</url>
        <connection>scm:git:github.com/j5ik2o/akka-persistence-s3</connection>
        <developerConnection>scm:git:git@github.com:j5ik2o/akka-persistence-s3.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <id>j5ik2o</id>
          <name>Junichi Kato</name>
        </developer>
      </developers>
  },
  publishTo := sonatypePublishToBundle.value,
  credentials := {
    val ivyCredentials = (baseDirectory in LocalRootProject).value / ".credentials"
    val gpgCredentials = (baseDirectory in LocalRootProject).value / ".gpgCredentials"
    Credentials(ivyCredentials) :: Credentials(gpgCredentials) :: Nil
  }
)

val coreSettings = Seq(
  organization := "com.github.j5ik2o",
  scalaVersion := scala213Version,
  crossScalaVersions ++= Seq(scala211Version, scala212Version, scala213Version),
  scalacOptions ++=
    Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-language:_",
      "-target:jvm-1.8"
    ) ++ crossScalacOptions(scalaVersion.value),
  resolvers ++= Seq(
      "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
      "Seasar Repository" at "https://maven.seasar.org/maven2/",
      "jitpack" at "https://jitpack.io"
    ),
  parallelExecution in Test := false,
  scalafmtOnCompile in ThisBuild := true
)

lazy val base = (project in file("base"))
  .settings(coreSettings)
  .settings(deploySettings)
  .settings(
    name := "akka-persistence-s3-base",
    libraryDependencies ++= Seq(
        "org.scala-lang"     % "scala-reflect"                   % scalaVersion.value,
        "com.iheart"        %% "ficus"                           % "1.5.0",
        "org.slf4j"          % "slf4j-api"                       % "1.7.30",
        "com.github.j5ik2o" %% "reactive-aws-s3-core"            % "1.2.6",
        "org.scalacheck"    %% "scalacheck"                      % "1.14.3"                   % Test,
        "ch.qos.logback"     % "logback-classic"                 % "1.2.3"                    % Test,
        "com.dimafeng"      %% "testcontainers-scala-scalatest"  % testcontainersScalaVersion % Test,
        "com.dimafeng"      %% "testcontainers-scala-localstack" % testcontainersScalaVersion % Test
      ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2L, scalaMajor)) if scalaMajor == 13 =>
          Seq(
            "com.typesafe.akka" %% "akka-slf4j"   % akka26Version,
            "com.typesafe.akka" %% "akka-stream"  % akka26Version,
            "com.typesafe.akka" %% "akka-testkit" % akka26Version % Test,
            "org.scalatest"     %% "scalatest"    % "3.1.1"       % Test
          )
        case Some((2L, scalaMajor)) if scalaMajor == 12 =>
          Seq(
            "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6",
            "com.typesafe.akka"      %% "akka-slf4j"              % akka26Version,
            "com.typesafe.akka"      %% "akka-stream"             % akka26Version,
            "com.typesafe.akka"      %% "akka-testkit"            % akka26Version % Test,
            "org.scalatest"          %% "scalatest"               % "3.1.1"       % Test
          )
        case Some((2L, scalaMajor)) if scalaMajor == 11 =>
          Seq(
            "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6",
            "com.typesafe.akka"      %% "akka-slf4j"              % akka25Version,
            "com.typesafe.akka"      %% "akka-stream"             % akka25Version,
            "com.typesafe.akka"      %% "akka-testkit"            % akka25Version % Test,
            "org.scalatest"          %% "scalatest"               % "3.0.8"       % Test
          )
      }
    }
  )

lazy val snapshot = (project in file("snapshot"))
  .settings(coreSettings)
  .settings(deploySettings)
  .settings(
    name := "akka-persistence-s3-snapshot",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2L, scalaMajor)) if scalaMajor == 13 =>
          Seq(
            "com.typesafe.akka" %% "akka-persistence"     % akka26Version,
            "com.typesafe.akka" %% "akka-persistence-tck" % akka26Version % Test
          )
        case Some((2L, scalaMajor)) if scalaMajor == 12 =>
          Seq(
            "com.typesafe.akka" %% "akka-persistence"     % akka26Version,
            "com.typesafe.akka" %% "akka-persistence-tck" % akka26Version % Test
          )
        case Some((2L, scalaMajor)) if scalaMajor == 11 =>
          Seq(
            "com.typesafe.akka" %% "akka-persistence"     % akka25Version,
            "com.typesafe.akka" %% "akka-persistence-tck" % akka25Version % Test
          )
      }
    }
  )
  .dependsOn(base % "test->test;compile->compile")

lazy val journal = (project in file("journal"))
  .settings(coreSettings)
  .settings(deploySettings)
  .settings(
    name := "akka-persistence-s3-journal",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2L, scalaMajor)) if scalaMajor == 13 =>
          Seq(
            "com.typesafe.akka" %% "akka-persistence"     % akka26Version,
            "com.typesafe.akka" %% "akka-persistence-tck" % akka26Version % Test
          )
        case Some((2L, scalaMajor)) if scalaMajor == 12 =>
          Seq(
            "com.typesafe.akka" %% "akka-persistence"     % akka26Version,
            "com.typesafe.akka" %% "akka-persistence-tck" % akka26Version % Test
          )
        case Some((2L, scalaMajor)) if scalaMajor == 11 =>
          Seq(
            "com.typesafe.akka" %% "akka-persistence"     % akka25Version,
            "com.typesafe.akka" %% "akka-persistence-tck" % akka25Version % Test
          )
      }
    }
  )
  .dependsOn(base % "test->test;compile->compile", snapshot % "test->comple")

lazy val root = (project in file("."))
  .settings(coreSettings)
  .settings(deploySettings)
  .settings(
    skip in publish := true
  )
  .aggregate(base, journal, snapshot)
