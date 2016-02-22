import sbt._
import sbt.Keys._
import spray.revolver.RevolverPlugin.Revolver

lazy val commonSettings = Seq(
	version := Settings.version,
	scalaVersion := Settings.versions.scalaV,
	scalacOptions ++= Settings.scalacOptions
)

// Akka Http agent
lazy val agent =
	(project in file("agent"))
	  .settings(commonSettings: _*)
	  .settings(
		  name := "metrics-visualizer-agent",
		  mainClass in Compile := Some("agent.Boot"),
		  mainClass in Revolver.reStart := Some("agent.Boot"),
		  libraryDependencies ++= Settings.commonDependencies.value
	  )

// Akka Http server
lazy val server =
	(project in file("server"))
	  .settings(commonSettings: _*)
	  .settings(Revolver.settings: _*)
	  .settings(
		  name := "metrics-visualizer-server",
		  libraryDependencies ++= Settings.serverDependencies.value,
		  mainClass in Compile := Some("server.Boot"),
		  // webapp task
		  resourceGenerators in Compile <+=
			(resourceManaged, baseDirectory, streams) map { (managedBase, base, _) =>
				val webappBase = base / "src" / "main" / "webapp"
				for {
					(from, to) <- webappBase ** "*" pair rebase(webappBase, managedBase / "main" / "webapp")
				} yield {
					Sync.copy(from, to)
					to
				}
			}
	  )

fork in run := true