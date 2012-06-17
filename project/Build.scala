import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "personal-page"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // https://github.com/typesafehub/play-plugins/tree/master/mailer
      "com.typesafe" %% "play-plugins-mailer" % "2.0.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" ** "style.less"),
      routesImport += "models.QueryBinders._"
    )

}
