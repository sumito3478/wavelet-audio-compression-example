import sbt._
import Keys._

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

object Build {
  lazy val root = project.in(file(".")).settings(
    name := "wavlet-audio-compression-example",
    version := "0.0.1-SNAPSHOT",
    organization := "info.sumito3478",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq(
      "-encoding", "utf-8",
      "-target:jvm-1.7",
      "-deprecation",
      "-feature",
      "-unchecked"),
    libraryDependencies ++= libs).settings(scalariformSettings: _*)

  lazy val scalariformSettings = SbtScalariform.scalariformSettings ++ Seq(
    SbtScalariform.ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(DoubleIndentClassDeclaration, true))

  lazy val libs = Seq(
    "com.nativelibs4java" %% "scalaxy-debug" % "0.3-SNAPSHOST" % "provided",
    "com.nativelibs4java" %% "scalaxy-loop" % "0.3-SNAPSHOST" % "provided"
  )
}