import com.typesafe.config.ConfigFactory

val conf = ConfigFactory.parseFile(new File("$play_module$/src/main/resources/application.conf")).resolve()

val commonSettings = Seq(
  scalaVersion := "2.12.3",
  version := conf.getString("app.version"),
  javaOptions in Test += "-Dlogger.resource=logback-test.xml"
)

name := """$name$"""

organization := "$organization$"

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(name := conf.getString("app.name") + "-root")
  .settings(
    run := {
      (run in playModule in Compile).evaluated
    },
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )
  .aggregate(playModule)

lazy val playModule = (project in file("$play_module$"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala, JavaServerAppPackaging, SwaggerPlugin)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    libraryDependencies ++= Dependencies.libraries,
    resolvers ++= Dependencies.resolvers,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    packageName in Docker := """$name$""",
    swaggerDomainNameSpaces := Seq("$package$.model"),
    coverageExcludedPackages := "<empty>;Reverse.*;.*Routes.*"
  )

addCommandAlias("run-local", ";project playModule;swagger;run -Dconfig.resource=application.conf -Dhttp.port=9000")

addCommandAlias("docker-snapshot", ";set isSnapshot in ThisBuild := true;docker:publishLocal")

fork in Test := true

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "$organization$.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "$organization$.binders._"
