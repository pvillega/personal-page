Due to my current work at [Google Summer of Code 2010](https://wiki.duraspace.org/display/GSOC/GSOC10+-+Add+Unit+Testing+to+Dspace) I've been setting up a small CI system for my personal use. Yes, it is an overkill, but I wanted to test it and, truth be told, the statistics provided by tools like [Sonar](http://www.sonarsource.org/) and [Hudson](http://hudson-ci.org/) are priceless, they show you many potential errors you may have in the code, coverage you are missing... to express it in my native tongue: "és la óstia nen"

I asked my friend Google for a guide on how to set them up together and it came up with this one. Easy five steps to set them up (check the link for more detail):

* **Step 1**: Download, install and start the Sonar Server.
* **Step 2**: Install and Configure Sonar plugin on Hudson.
* **Step 3**: Configure your Hudson Job
* **Step 4**: Build your project
* **Step 5**: Browse to Sonar Dashboard (default user is admin/admin)

And you have your CI environment ready to go.  Easy. Simple. Powerful. And useful!

I recommend you to use the following Sonar plugins:

+ [Eclipse Plugin][1]
+ [Tag List Plugin][2]
+ [Total Quality Plugin][3]
+ [SCM Activity Plugin][4]
+ [Sonar PDF Plugin][5]
+ [Motion Chart Plugin][6]
+ [Build Breaker][7]

With them you will get some extra data in your reports, you will be able to access them from your IDE (more useful for developers) and the builds will generate a handy pdf report for your project manager. Many benefits for a few customizations. You don't even need to change the default settings for any standard project.


  [1]: http://docs.codehaus.org/display/SONAR/Installing+Sonar+in+Eclipse
  [2]: http://docs.codehaus.org/display/SONAR/Taglist+Plugin
  [3]: http://docs.codehaus.org/display/SONAR/Total+Quality+Plugin
  [4]: http://docs.codehaus.org/display/SONAR/SCM+Activity+Plugin
  [5]: http://docs.codehaus.org/display/SONAR/Sonar+PDF+Plugin
  [6]: http://docs.codehaus.org/display/SONAR/Motion+Chart+plugin
  [7]: http://docs.codehaus.org/display/SONAR/Build+Breaker+Plugin