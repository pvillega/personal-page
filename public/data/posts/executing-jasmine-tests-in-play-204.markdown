*Christmas* break is over, and on a whim I started to check [AngularJs](http://angularjs.org/). If you haven't tested it yet, do it. I've toyed around  with Backbone and some other Javascript *single page app* frameworks, and Angular is by far the one that I liked the most. It has simplicity embedded in its core, and turning tedious tasks (as setting controllers) into something trivial makes it a pleasure to use. Simplicity. Such an important concept, and one which is seldom present in frameworks.

But I digress. Back to the post. Adding Angular to a Play app is as simple as you can imagine, as in the end it's a Javascript library. Add the Angular files, code controllers and services, put Angular templates under `public` so Angular can get them, and you are done.  You can see a project sample [in my Github account](https://github.com/pvillega/play2demo-jasmine). Be aware the sample is very rough and has minimal functionality, the aim was to see how can they be linked, not to push Angular boundaries.

So we have a Javascript framework, and obviously we want to test it. Unfortunately the recommended way to test Angular is via [Jasmine](http://pivotal.github.com/jasmine/), a Javascript BDD framework. I say unfortunately because that would mean running 2 commands for testing: one for Play tests and another for Angular tests. Or does it?

It is on moments like this when Open Source shows its worthiness. Enter [The Guardian](http://www.guardian.co.uk/) and its IT department, which open sources a lot of the code they create to run their online platform. A wise company, they use Scala and Play and they created [Sbt-Jasmine](https://github.com/guardian/sbt-jasmine-plugin), a plugin to run Jasmine inside Sbt projects. Given that Play 2 uses Sbt, this should prove simple.

Well, to be honest it wasn't *so* simple, that's why I decided to document it here to save time to other interested people. This assumes a working Play 2.0.4 project, should work with any 2.0.x project but can't promise anything about 2.1 (have to test it yet)

###Integrating Sbt-Jasmine

This is quite straightforward, you just need to follow the instructions from the [Sbt-Jasmine](https://github.com/guardian/sbt-jasmine-plugin) page. On your project, under the `project` folder, create another folder named `project` (that is, you will have `/project/project/` path). Inside that new folder create a file `Plugins.scala` and add this code to it:

	import sbt._

	// This plugin is used to load the sbt-jasmine plugin into our project. This allows us to import the SbtJasminePlugin file
	// in Build.scala, and then set the settings and configuration for Sbt-Jasmine
	object Plugins extends Build {
	  lazy val plugins = Project("plugins", file("."))
	    .dependsOn(uri("git://github.com/guardian/sbt-jasmine-plugin.git#0.7"))
	}

This will load the plugin directly from Github into your project. 

###Adding Jasmine  as part of your test cycle

Now that we have the plugin in, we want to be able to run both test types (Play and Jasmine) when executing `play test`. To that aim we have to edit our `Build.scala` project file to let Play know about Jasmine. 

The first step will be importing the existing Jasmine settings the sbt plugin added into the project. We can do that via the `PlayProject` method `settings`, as in `.settings(jasmineSettings : _*)`

The second step is to override the default Jasmine configuration, specifically the paths that point to the test assets and source files. Sbt-Jasmine expects a set of default paths that don't map to the ones existing in a Play project, so we change them as follows:

	// jasmine configuration, overridden as we don't follow the default project structure sbt-jasmine expects
        appJsDir <+= baseDirectory / "app/assets/javascripts",
        appJsLibDir <+= baseDirectory / "public/javascripts/lib",
        jasmineTestDir <+= baseDirectory / "test/assets/",
        jasmineConfFile <+= baseDirectory / "test/assets/test.dependencies.js",

Note that `appJsDir` is pointing to the folder for minimized assets, we could change that to point to `"public/Javascripts"`. The paths for `appJsLibDir`, `jasmineTestDir` and `jasmineConfFile` can be modified as it suits you.

The Jasmine configuration file follows standard Jasmine notation, which I won't discuss in this post. The configuration for this sample project is:

	// Dependencies for the unit test via Jasmine
	EnvJasmine.loadGlobal(EnvJasmine.libDir + "jquery-1.8.3.min.js");
	EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular.min.js");
	EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular-resource.min.js");
	EnvJasmine.loadGlobal(EnvJasmine.testDir + "config/angular-mocks.js");

	// Import all modules (in theory this should be done with RequireJs, but it fails for some reason)
	// FIXME: check why we can't use RequireJs instead of manually importing files
	EnvJasmine.loadGlobal(EnvJasmine.rootDir + "todo.js");


Finally we need to tell Play to run Jasmine tests when executing the `test` command. We do this by adding a dependency on `test` with  `(test in Test) <<= (test in Test) dependsOn (jasmine)`. This will cause Play to run first the Jasmine tests and, if they succeed, Play own tests will be executed.

The final code looks like:

	val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA)
	      .settings(jasmineSettings : _*)  //this adds jasmine settings from the sbt-jasmine plugin into the project
	      .settings(
	      // Add your own project settings here

	      // jasmine configuration, overridden as we don't follow the default project structure sbt-jasmine expects
	      appJsDir <+= baseDirectory / "app/assets/javascripts",
	      appJsLibDir <+= baseDirectory / "public/javascripts/lib",
	      jasmineTestDir <+= baseDirectory / "test/assets/",
	      jasmineConfFile <+= baseDirectory / "test/assets/test.dependencies.js",
	      // link jasmine to the standard 'sbt test' action. Now when running 'test' jasmine tests will be run
	      // and after that other Play tests will be executed.
	      (test in Test) <<= (test in Test) dependsOn (jasmine)
	    )

And that's all. If you add some Jasmine tests under `test/assets` and then execute `play test` you will see them being run. Remember what I said about simplicity? Why should you remember to run 2 suits independently when 1 command can do that for you?

###To be improved

As much as I would like, this is not perfect. I found a couple of issues when integrating Jasmine and Play. Given I'm not an expert on neither, any help will be appreciated:

- In theory you could use [RequireJS](http://requirejs.org/) to import the Javascript sources to test, but it didn't work when I tried. Not sure why and for a standard AngularJs distribution we are talking about 5 Javascript files (it could grow if you break them into components), so it doesn't seem such a big deal. But it's not perfect.

- AngularJs allows you to declare End to End test scenarios, similar to Selenium but inside AngularJs itself. Unfortunately I couldn't make them work, as they require a server running. Given the enhanced Selenium support in newer AngularJS versions I expect one could use Play end to end scenarios to test this part, but I need to put more work on it to ensure it is true.

If anyone knows how to fix this, please let me know!