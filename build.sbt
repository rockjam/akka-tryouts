import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._

name := "scala-ws"
version := "1.0"
sbtVersion := "0.13.7"
scalaVersion := "2.11.4"
mainClass := Some("Main")
incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(true)
promptTheme := ScalapenosTheme
javaOptions ++= Seq(
  "-XX:+UseParallelGC",
  "-noverify"
)
scalacOptions ++= Seq(
  "-Ydelambdafy:method",
  "-target:jvm-1.7",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)
resolvers ++=  Seq(
  "spray repo" at "http://repo.spray.io/"
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.7" exclude ("org.scala-lang" , "scala-library"),
  "io.spray" % "spray-can_2.11" % "1.3.2",
  "io.spray" % "spray-routing_2.11" % "1.3.2",
  "io.spray" % "spray-json_2.11" % "1.3.1" exclude ("org.scala-lang" , "scala-library"),
  "com.wandoulabs.akka" %% "spray-websocket" % "0.1.3",
  "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test"
)
seq(Revolver.settings: _*)

addCommandAlias("start", ";re-start;~products")
