From time to time I like to test some new functionality of [Play 2.0](http://www.playframework.org/). Creating a tiny for-fun project allows me to see that functionality in action, to solve the initial pitfalls related to it and to have a basic sample for later on.

With Play 2.1 coming closer, and seeing all the new stuff in it (Slick, Scala 2.10, etc) I wanted to test some of the features that I've not been able to use in a "real project", especially the ones related to Real Time web. When thinking about what to implement I remembered an issue a friend had in his company, an ETL-like process that had to receive data (massive amounts) on one end and store it in several location after processing the data. While not in the mood to create a real system which would be quite complex, having to manage queues and ensuring messages were delivered, that inspired me to plan a minor project in which I could use several interesting technologies: Iteratee, MongoDB, Redis and Akka actors. I've not used any of them very often, so I was sold to the idea :)

You can find the code resulting from this experiment in [my Github repository](https://github.com/pvillega/iteratees-test).

I won't start a line-by-line description of the project, the source is there and I believe it's quite self-explanatory. But there are some things that are worth mentioning, even if it is just for future reference:

* **Enumerator.imperative** allows you to push data into the Enumerator manually **and won't close the stream** until you say so. I had problems when trying to implement a Html view on the stream, as a non-imperative stream was closing automatically once the data was consumed, not waiting for the next input which was slower than the rate at which the app consumed the Enumerator.

* [SSE](http://dev.w3.org/html5/eventsource/) is great, very performing and with better support than WebSockets. Also, the fact that the browser automatically restores the connection if the link is dropped simplifies the code a lot. But (always a but!) it has a bug in its implementation in Play 2.0.4 where the events won't have an Id associated even if you provide the corresponding extractor. Hopefully that will be solved in 2.1. The event name part works, though!

* **Concurrent.hub** has a nice trick with the `getPatchCord` method where you multiplex the stream to several clients at the same time. Saves a lot of memory and CPU.

* **Akka** actors are as simple, fast and reliable as always. As a *veteran* Java developer, seeing Actors makes it very very hard to go back to *synchronized* and all that clutter

* The combination Iteratee + Actor + MongoDB is **FAST**. When running a test with simulated request every 10ms my computer (a bit old by nowadays standards) didn't notice it at all. Low resource consumption and high speed? Go Play! 

* Talking about **MongoDB**, I've never been a huge fan of NoSQL and in the past I read many articles which made me doubt about Mongo. Obviously, this is not a real-life system where I can't afford to lose data, but it was refreshing to see the simplicity of the approach Mongo takes to storing and retrieving data. With [ReactiveMongo](http://reactivemongo.org/) and Play 2.1 it may be a wonderful support system for non-critical real-time data. 

* **Redis** as a Memcached replacement is good, very good, but the additional methods provided by the API so you can use it as a *key store* can be a bit confusing. I can't say which one is better (Redis or Memcached) but I'm quite impressed with Redis.

Not much more to add. Feel free to clone the code and play with it. It's nothing amazing as code goes, but it shows what you can achieve with just a few lines of Scala and Play :)