organization := "de.digitec"

name := "enum"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.3"
// TODO CDO: Cross versions
crossScalaVersions := Seq("2.12.3")


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


