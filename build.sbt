organization := "de.digitec"

name := "denumeration"

version := "0.5.2-SNAPSHOT"

scalaVersion := "2.12.10"
crossScalaVersions := Seq("2.12")

val JmhVersion = "1.22"
val ScalaCheckVersion = "1.14.3"
val JUnitVersion = "4.13"

libraryDependencies += "org.openjdk.jmh" % "jmh-core" % "1.22"
libraryDependencies += "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test
libraryDependencies += "junit" % "junit" % JUnitVersion % Test

enablePlugins(JmhPlugin)


/** Tests: */
parallelExecution in Test := false

/** Publishing to Sonatype: */
publishMavenStyle := true
publishArtifact in Test := false
publishTo := Some(Resolver.mavenLocal)

pomExtra := {
   <url>digitec.de</url>
   <licenses>
      <license>
         <name>The Apache Software License, Version 2.0</name>
         <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
         <distribution>repo</distribution>
      </license>
   </licenses>
}


