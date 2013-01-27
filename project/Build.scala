import sbt._
import Keys._
import PlayProject._
import cloudbees.Plugin._

object ApplicationBuild extends Build {

    val appName         = "personal-page"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // https://github.com/typesafehub/play-plugins/tree/master/mailer
      "com.typesafe" %% "play-plugins-mailer" % "2.0.2",
      // MakdownJ https://github.com/myabc/markdownj
      "org.markdownj" % "markdownj" % "0.3.0-1.0.2b4"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA)
      .settings(cloudBeesSettings :_*)
      .settings(
      // Add your own project settings here
      lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" ** "style.less"),

      CloudBees.applicationId := Some("PersonalPage")
    )

}
