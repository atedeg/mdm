val scala3Version = "3.2.0-RC2"
val scalaTestVersion = "3.2.12"

ThisBuild / organization := "dev.atedeg"
ThisBuild / scalaVersion := scala3Version

ThisBuild / scalacOptions += "-language:strictEquality"

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  )
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "mdm"
  )