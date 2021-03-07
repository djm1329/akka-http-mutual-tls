name := "akkahttp-server-sample"

version := "1.0"

scalaVersion := "2.13.5"
lazy val akkaVersion = "2.6.13"
lazy val akkaHttpVersion = "10.2.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion, 
  "com.typesafe.akka" %%  "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
