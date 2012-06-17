Personal Page
=============

This application can be used to create a Personal Page.

A Personal Page is not really a blog, although it contains posts. It's aimed to people who wants their own space in the web to store information, show what they've done and (maybe) post from time to time but not enough as to keep a blog.

So, basically, something I wanted and now I built :)

## Components

This application uses:

+ Play Framework 2.0.1 w/ Scala
+ Bootstrap 2.0.4
+ JQuery 1.7.1
+ [Mail plugin][1]
+ [Memcache plugin][2]
+ [Pjax][9]
+ [Tweet][10]
+ Other components I may have forgotten to mention

This software is ready to be used in Heroku, but it can be adapted for other platforms easily.

## How to deploy in Heroku

No DB is required to deploy the application. You will require a Heroku mail addon and a Heroku memcache addon for this site, otherwise you'll get critical errors. You may disable them if you don't want to use them.

The Procfile command uses a custom variable (**MYCONFIG**) that is expected to contain additional Java environment values. For example:

> MYCONFIG=-Dmail.username=user -Dmail.password=password

You can edit the Procfile to change the way this works.


## How to change the contents

Some strings are internationalized, check *conf/messages* to see the list. To alter the layout you'll need toe dit the templates and less files, no fast-track for that.

The following sections explain how to modify the content in some sections.

### Index Page

The following places contain the information displayed in the index page

- The avatar displayed in the top-left area can be configured at **application.conf** under the *avatar.url* key. If the image is a local resource, the path must be the external url (*/assets/images/...*)
- There is a file *whoami.markdown* under **/public/data/**. Content added to this file (markdown format) will be displayed in the top-left area of the page
- The tag file *contact.scala.htm* located under **app/views/tag/** contains the contact details displayed on the main page
- The tag file *trivia.scala.htm* located under **app/views/tag/** contains the trivia details displayed on the main page
- Projects and posts are retrieved from the list of Posts and Projects, see below

### Sidebar

To modify the contents of the sidebar shown in some areas (like Projects or About) edit the tag *sidebar.scala.html* located under **app/views/tag/**

### Others

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
[2]: https://github.com/mumoshu/play2-memcached
[3]: http://nesbot.com/2011/11/22/now-running-on-play-2-beta
[4]: https://github.com/briannesbitt/nesbot.com
[5]: http://erjjones.github.com/blog/How-I-built-my-blog-in-one-day/
[6]: http://kev.inburke.com/
[7]: https://bitbucket.org/kevinburke/blog-design/src/8119db77e1c1/LICENSE
[8]: http://theoatmeal.com/
[9]: https://github.com/defunkt/jquery-pjax
[10]: https://github.com/seaofclouds/tweet

