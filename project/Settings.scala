import sbt._

/**
  * Application settings. Configure the build for your application here.
  * You normally don't have to touch the actual build definition after this.
  */
object Settings {

	/** The version of your application */
	val version = "0.0.1"

	/** Options for the scala compiler */
	val scalacOptions = Seq(
		"-Xlint",
		"-unchecked",
		"-deprecation",
		"-feature",
		"-language:postfixOps",
		"-language:implicitConversions"
	)

	/** Declare global dependency versions here to avoid mismatches in multi part dependencies */
	object versions {
		val scalaV = "2.11.7"

		val akkaV = "2.4.2"
		val reactiveMongoV = "0.11.9"

		val scalaTestV = "2.2.6"
		val scalaMockV = "3.2.2"

		val scaldiV = "0.5.7"
	}

	import versions._

	val testDependencies = Def.setting(Seq(
		"org.scalatest" %% "scalatest" % scalaTestV % "test",
		"org.scalamock" %% "scalamock-scalatest-support" % scalaMockV % "test"
	))

	val commonDependencies = Def.setting(Seq(
		"com.typesafe.akka" %% "akka-actor" % akkaV,
		"com.typesafe.akka" %% "akka-stream" % akkaV,
		"com.typesafe.akka" %% "akka-http-core" % akkaV,
		"com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV
	))

	val serverDependencies = Def.setting(commonDependencies.value ++ Seq(
		"com.typesafe.akka" %% "akka-http-testkit" % akkaV,
		"org.reactivemongo" %% "reactivemongo" % reactiveMongoV,
		"org.scaldi" %% "scaldi" % scaldiV
	) ++ testDependencies.value)

}