Personal Page
=============

This application can be used to create a Personal Page.

A Personal Page is not really a blog, although it contains posts. It's aimed to people who wants their own space in the web to store information, show what they've done and (maybe) post from time to time but not enough as to keep a blog.

So, basically, something I wanted and now I built :)

## Components

This application uses:

+ Play Framework 2.0.4 w/ Scala
+ Bootstrap 2.2.1
+ JQuery 1.8.1
+ [Mail plugin][1]
+ [Tweet][10]
+ Other components I may have forgotten to mention

This software is ready to be used in Heroku, but it can be adapted for other platforms easily.

## How to deploy in Heroku

No DB is required to deploy the application. You will require a Heroku mail addon and a Heroku memcache addon for this site, otherwise you'll get critical errors. You may disable them if you don't want to use them.

The Procfile command uses a custom variable (**MYCONFIG**) that is expected to contain additional Java environment values. For example:

> MYCONFIG=-Dmail.username=user -Dmail.password=password

You can edit the Procfile to change the way this works.

## Edit content

In dev mode you can access the administration area that allows you to edit content like About, Posts, Link Dump, etc.

Using this area all the json and markdown files are updated automatically, you just need to be sure to commit them to Git.

If you add images to the markdown, you'll have to store them manually under **public/images** and provide the external link (**assets/images/...**) to the document.

## How to publish new content

Follow these steps:
+ edit the corresponding json/markdown files as described above
+ commit to Git
+ publish into Heroku

## Demo

See my own page at <http://www.perevillega.com>


## Credit to

Brian Nesbitt for his [Play 2.0 Beta blog system][3] ([source code][4])

Eric Jones for his post on [building your own blog][5]

Kevin Burke for his [awesome layout][6] which he let me copy shamelessly

[The Oatmeal][8] for his awesome tumbeasts


## License

Copyright (c) 2012 Pere Villega

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


The Theme is based in Kevin Burke's theme for Wordpress (with minor modifications), [licensed as follows][7]:


Copyright (c) 2012 Kevin Burke

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



[1]: https://github.com/typesafehub/play-plugins/tree/master/mailer
[3]: http://nesbot.com/2011/11/22/now-running-on-play-2-beta
[4]: https://github.com/briannesbitt/nesbot.com
[5]: http://erjjones.github.com/blog/How-I-built-my-blog-in-one-day/
[6]: http://kev.inburke.com/
[7]: https://bitbucket.org/kevinburke/blog-design/src/8119db77e1c1/LICENSE
[8]: http://theoatmeal.com/
[10]: https://github.com/seaofclouds/tweet

