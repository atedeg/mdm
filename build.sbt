val scala3Version = "3.1.3"
val scalaTestVersion = "3.2.12"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "dev.atedeg"
ThisBuild / homepage := Some(url("https://github.com/atedeg/mdm"))
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "_includes"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

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

ThisBuild / wartremoverErrors ++= Warts.allBut(Wart.Overloading)

ThisBuild / scalafixDependencies ++= Seq(
  "com.github.xuwei-k" %% "scalafix-rules" % "0.2.1",
)

ThisBuild / semanticdbEnabled := true

lazy val startupTransition: State => State = "conventionalCommits" :: _
Global / onLoad := { startupTransition compose (Global / onLoad).value }

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "eu.timepit" %% "refined" % "0.10.1",
    "org.typelevel" %% "cats-core" % "2.8.0",
    "org.typelevel" %% "cats-mtl" % "1.3.0",
    "org.typelevel" %% "shapeless3-deriving" % "3.1.0",
  ),
)

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    name := "mdm",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    ScalaUnidoc / unidoc / target := file("target/site"),
    Compile / doc / scalacOptions := Seq(
      "-project",
      "MDM - Mambelli Domain Model",
      "-project-version",
      version.value,
      "-project-footer",
      "Made with ❤ by Giacomo, Nicolas, Nicolò, Linda",
      "-siteroot",
      "docs/",
      "-doc-root-content",
      "docs/api.md",
    ),
    jacocoAggregateReportSettings := JacocoReportSettings(
      title = "mdm coverage report",
      formats = Seq(JacocoReportFormats.XML),
    ),
  )
  .aggregate(utils)

lazy val utils = project
  .in(file("utils"))
  .settings(commonSettings)

lazy val `products-shared-kernel` = project
  .in(file("products-shared-kernel"))
  .settings(commonSettings)
  .dependsOn(utils)
