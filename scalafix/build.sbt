lazy val V = _root_.scalafix.sbt.BuildInfo
inThisBuild(
  List(
    organization := "ch.megard",
    scalaVersion := V.scala212,
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List(
      "-Yrangepos",
      "-P:semanticdb:synthetics:on"
    )
  )
)

skip in publish := true

lazy val rules = project.settings(
  moduleName := "scalafix",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val input = project.settings(
  skip in publish := true,
  libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.4.2",
  libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.3",
)

lazy val output = project.settings(
  skip in publish := true,
  libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.5.0-SNAPSHOT",
  libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.3",
)

lazy val tests = project
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) := 
      compile.in(Compile).dependsOn(compile.in(input, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(output, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(input, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(input, Compile).value,
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
