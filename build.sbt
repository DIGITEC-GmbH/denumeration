organization := "de.digitec"

name := "enumerator"

version := "0.1"

scalaVersion := "2.12.10"
// TODO CDO: Cross versions
crossScalaVersions := Seq("2.12", "2.13")


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


