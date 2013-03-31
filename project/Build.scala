import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "personal-page"
    val appVersion      = "1.2"

    val appDependencies = Seq(
      // MakdownJ https://github.com/myabc/markdownj
      "org.markdownj" % "markdownj" % "0.3.0-1.0.2b4"
    )

    val main = play.Project(appName, appVersion, appDependencies)
      .settings(
      // Add your own project settings here
      lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" ** "style.less")
    )

}
