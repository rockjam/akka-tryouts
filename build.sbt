name := "scala-ws"
version := "1.0"
sbtVersion := "0.13.7"
scalaVersion := "2.11.2"
mainClass := Some("Main")
scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)
resolvers ++=  Seq(
  "spray repo" at "http://repo.spray.io/"
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.5",
  "io.spray" % "spray-caching_2.11" % "1.3.1",
  "io.spray" % "spray-can_2.11" % "1.3.1",
  "io.spray" % "spray-client_2.11" % "1.3.1",
  "io.spray" % "spray-routing_2.11" % "1.3.1",
  "io.spray" % "spray-json_2.11" % "1.2.6",
  "com.wandoulabs.akka" %% "spray-websocket" % "0.1.3",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)
seq(Revolver.settings: _*)
