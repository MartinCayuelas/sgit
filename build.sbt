name := "sgit"

version := "0.1"

scalaVersion := "2.13.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"

libraryDependencies += "org.mockito" %% "mockito-scala" % "1.5.18"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

import sbtsonar.SonarPlugin.autoImport.sonarProperties

sonarProperties ++= Map(
  "sonar.host.url" -> "http://localhost:9000",
  "sonar.sources" -> "src/main/scala",
  "sonar.tests" -> "src/test/scala"
)

parallelExecution in Test := false