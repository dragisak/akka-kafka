name := "akka-kafka"

version := "0.1"

scalaVersion := "2.13.7"

val akkaVersion         = "2.6.17"
val alpakkaKafkaVersion = "2.1.1"
val jacksonVersion      = "2.11.4"
val vulcanVersion       = "1.7.1"
val circeVersion        = "0.14.1"
val catsVersion         = "2.6.1"

libraryDependencies ++= List(
  "com.typesafe.akka"         %% "akka-stream-kafka"        % alpakkaKafkaVersion,
  "com.typesafe.akka"         %% "akka-stream"              % akkaVersion,
  "com.fasterxml.jackson.core" % "jackson-databind"         % jacksonVersion,
  "com.github.fd4s"           %% "vulcan"                   % vulcanVersion,
  "com.github.fd4s"           %% "vulcan-generic"           % vulcanVersion,
  "io.circe"                  %% "circe-core"               % circeVersion,
  "io.circe"                  %% "circe-generic"            % circeVersion,
  "io.circe"                  %% "circe-parser"             % circeVersion,
  "org.typelevel"             %% "cats-core"                % catsVersion,
  "com.github.pureconfig"     %% "pureconfig"               % "0.17.0",
  "org.scalatest"             %% "scalatest-wordspec"       % "3.2.10"   % Test,
  "org.scalatest"             %% "scalatest-shouldmatchers" % "3.2.10"   % Test,
  "org.scalacheck"            %% "scalacheck"               % "1.15.4"   % Test,
  "org.scalatestplus"         %% "scalacheck-1-15"          % "3.2.10.0" % Test
)
