Personal Page
=============

This application can be used to create a Personal Page.

A Personal Page is not really a blog, although it contains posts. It's aimed to people who wants their own space in the web to store information, show what they've done and (maybe) post from time to time but not enough as to keep a blog.

So, basically, something I wanted and now I have a excuse to build :)

## Components

+ Play Framework 2.0.1 w/ Scala
+ Bootstrap 2.0.4
+ [Mail plugin][1]
+ [Memcache plugin][2]


## How to deploy in Heroku

No DB is required to deploy in Heroku. You will require a mail addon and a memcache addon for this site to work.

The Procfile command uses a custom variable (**MYCONFIG**) that is expected to contain additional Java environment values. For example:

> MYCONFIG=-Dmail.username=user -Dmail.password=password

You can edit the file to change the way this works.


## Credit to

Brian Nesbitt for his inspiration with his [Play 2.0 Beta blog system][3] ([source code][4])

Eric Jones for his post on [building your own blog][5]

Kevin Burke for his [awesome layout][6] which he let me copy shamelessly

[The Oatmeal][8] for his awesome tumbeasts


## License

Copyright (c) 2012 Pere Villega

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


The Theme is Based in Kevin Burke's theme for Wordpress, [licensed as follows][7]:

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

