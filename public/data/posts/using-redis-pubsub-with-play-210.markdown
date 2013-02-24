Last week I was experimenting a bit with [Redis](http://redis.io/) and its [Publish-Subscribe](http://redis.io/topics/pubsub) module. The idea was to try to implement a chat with it (something I'll need for my next project) and from all the options I evaluated this seemed the best. Loving Redis so far, whoever called it the swiss-knife of databases was completely right.

As to be expected with programming I had some trouble, in this scenario using Redis PubSub with Play 2.1.0 blocked the application. So I decided to publish this in case someone else has the same issue. If you don't want to read the details just go to the [GitHub repository](https://github.com/pvillega/play21-redis-pubsub) and clone the source. It contains a very simple application in which Play subscribes to a Redis channel and sends messages to it via Akka actors, while using a listener to notify about reception of the same messages. The `Readme` file gives more detail on how it works.

So now let's talk about Redis and the issue I had.

## Using Redis with Play

Using Redis with Play is very straightforward. Typesafe provides a [play plugin](https://github.com/typesafehub/play-plugins/tree/master/redis) to manage the interactions. The plugin is based on [Sedis](https://github.com/pk11/sedis), the Scala library for Redis, which in turn is a wrapper over [Jedis](https://github.com/xetorthio/jedis/), the most popular JVM library for Redis.

At the time of this writting the dependencies for Play 2.1 are not working perfectly so you need to add an additional resolver to your project to be able to download all of them:

    resolvers += "org.sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"

but once done you can use Redis as both Cache layer and Database for Play.


## The issue

Simple as it is, things got a bit more complex when using PubSub. The way PubSub works with Redis is as follows: 

* first of all you set a client to subscribe to a channel. The subscription call accepts a listener class that will trigger method calls as response to events in the channel (messages being passed around, etc)
* then you need clients to send messages to the channel via the `publish` operation.

So the resulting code for subscription would be something like this:

    pool.withJedisClient{ client =>
       client.subscribe(listener, CHANNEL)
    }

but as it happens `subscribe` is a blocking call. This means that every time we execute it, a thread will be locked into receiving notifications from Redis. And the result is that Play will stop working as all threads get consumed by calls to this operation.

## Solution

Thankfully Play 2.1.0 introduces a new concept that solves this issue: [ExecutionContexts](http://www.playframework.com/documentation/2.1.0/ThreadPools). An `ExecutionContext` is nothing more than a set of threads, independent from the ones managing the Play app itself, to be used when we have slow or blocking operations. This ensures some specialized thread takes care of that operation without impacting the performance of the application itself.

In our case, as we have a blocking call, we simply want to run the `subscribe` operation inside its own `ExecutionContext`, which will consist on threads devoted to listening to Redis, while the standard `ExecutionContext` manages other Play calls. So our code will become as follows:


    // Execution context used to avoid blocking on subscribe
    object Contexts {
       implicit val myExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("akka.actor.redis-pubsub-context")
    }

    // subscribe in a Future using a specific ExecutionContext    
    Future {
      // use Sedis pool to launch the subscribe operation
      pool.withJedisClient{ client =>
           client.subscribe(listener, CHANNEL)
      }
    }(Contexts.myExecutionContext)


And that's it. A simple way to avoid blocking operations slowing your app or consuming all the threads it needs. If you've had this issue this will solve it for you.