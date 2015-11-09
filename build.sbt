name := """analyzer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "jp.t2v" %% "play2-auth"        % "0.14.1",
  "jp.t2v" %% "play2-auth-social" % "0.14.1",
  "jp.t2v" %% "play2-auth-test"   % "0.14.1" % "test",
  play.sbt.Play.autoImport.cache,
  "org.webjars" % "bootstrap" % "3.3.4",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24",
  "org.mindrot"           % "jbcrypt"                           % "0.3m"
)
libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.1.1-2"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
)

resolvers += "mandubian maven bintray" at "http://dl.bintray.com/mandubian/maven"

libraryDependencies ++= Seq(
  "com.mandubian"     %% "play-json-zipper"    % "1.2"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
