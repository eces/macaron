organization := "com.trinity"

name := "macaron"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.scala-sbt" % "command" % "0.12.3",
  "org.scala-sbt" % "launcher-interface" % "0.12.4",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2",
  "jline" % "jline" % "2.10"
)

sbtPlugin := true

fork := true
// fork in (Compile, run) := true

outputStrategy := Some(StdoutOutput)

connectInput in run := true

// mainClass in (Compile, run) := Some("com.trinity.Example")
mainClass in (Compile, run) := Some("com.trinity.Console")