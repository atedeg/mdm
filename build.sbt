val scala3Version = "3.2.0-RC2"
val scalaTestVersion = "3.2.12"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "dev.atedeg"
ThisBuild / homepage := Some(url("https://github.com/atedeg/mdm"))
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

ThisBuild / developers := List(
  Developer(
    "giacomocavalieri",
    "Giacomo Cavalieri",
    "giacomo.cavalieri@icloud.com",
    url("https://github.com/giacomocavalieri"),
  ),
  Developer(
    "nicolasfara",
    "Nicolas Farabegoli",
    "nicolas.farabegoli@gmail.com",
    url("https://github.com/nicolasfara"),
  ),
  Developer(
    "ndido98",
    "Nicolò Di Domenico",
    "ndido98@gmail.com",
    url("https://github.com/ndido98"),
  ),
  Developer(
    "vitlinda",
    "Linda Vitali",
    "lindav94vitali@gmail.com",
    url("https://github.com/vitlinda"),
  ),
)

ThisBuild / scalacOptions += "-language:strictEquality"

ThisBuild / wartremoverErrors ++= Warts.all

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
