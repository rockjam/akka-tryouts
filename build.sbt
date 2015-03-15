import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._

promptTheme := ScalapenosTheme

commonSetting

lazy val commonSetting = Seq(
  resolvers ++= Seq(
    "spray repo" at "http://repo.spray.io/"
  ),
  version := "1.0",
  sbtVersion := "0.13.8-RC1",
  scalaVersion := "2.11.6",
  incOptions := incOptions.value.withNameHashing(true),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

val app = crossProject.
  settings(commonSetting: _*).
  settings(
    name := "scala-ws",
    unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "main" / "scala"
  ).jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    )
  ).jvmSettings(
    javaOptions ++= Seq(
      "-XX:+UseConcMarkSweepGC",
      "-noverify"
    ),
    scalacOptions ++= Seq(
      "-Ydelambdafy:method",
      "-target:jvm-1.7",
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"
    ),
    mainClass := Some("Main"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.3.9" exclude("org.scala-lang", "scala-library"),
      "io.spray" %% "spray-can" % "1.3.2",
      "io.spray" %% "spray-routing" % "1.3.2",
      "io.spray" %% "spray-json" % "1.3.1" exclude ("org.scala-lang" , "scala-library"),
      "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    )
  )

lazy val appJS = app.js
lazy val appJVM = app.jvm.
  settings(Revolver.settings: _*).
  settings(
    (resources in Compile) += (fastOptJS in(appJS, Compile)).value.data
  )

//addCommandAlias("start", ";appJVM/re-start; ~appJVM/products~appJVM/products")
addCommandAlias("start", ";appJVM/re-start")