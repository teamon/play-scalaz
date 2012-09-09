organization := "eu.teamon"

name := "play-scalaz"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")

resolvers ++= Seq(
  Resolver.url("Play", url("http://download.playframework.org/ivy-releases/"))(Resolver.ivyStylePatterns),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "play" %% "play" % Option(System.getenv("PLAY_VERSION")).getOrElse("2.0") % "compile",
  "org.scalaz" % "scalaz-core_2.9.2" % "7.0.0-M3",
  "org.scalaz" % "scalaz-typelevel_2.9.2" % "7.0.0-M3"
)

seq(scalajarsSettings:_*)

scalajarsProjectName := "play-scalaz"
