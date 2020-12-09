// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `akka-source-queue-bug` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.akkaSlf4j,
        library.akkaStream,
        library.akkaStreamTestkit % Test,
        library.munit             % Test,
        library.munitScalaCheck   % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka  = "2.6.9"
      val munit = "0.7.19"
    }
    val akkaSlf4j         = "com.typesafe.akka" %% "akka-slf4j"          % Version.akka
    val akkaStream        = "com.typesafe.akka" %% "akka-stream"         % Version.akka
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka
    val munit             = "org.scalameta"     %% "munit"               % Version.munit
    val munitScalaCheck   = "org.scalameta"     %% "munit-scalacheck"    % Version.munit
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.4",
    organization := "au.com.titanclass",
    organizationName := "Titan Class P/L",
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    startYear := Some(2020),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-encoding",
      "UTF-8",
      "-Ywarn-unused:imports"
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    scalafmtOnCompile := true,
    Compile / compile / wartremoverWarnings ++= Warts.unsafe
  )
