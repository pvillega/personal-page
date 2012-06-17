Soon Java 7 will be released. A bit delayed, yes, but it's here. Even so the question lingers: is it enough? I believe the answer nowadays is NO, but of course this is an open discussion in which each developer will have a different (and valid) opinion as per each one's background. Let's give some arguments for the no, then. Let's talk about [Scala](http://www.scala-lang.org/). 

## Why Scala?

You may be wondering why should you learn a new language. If you want to be the average run-of-the-mill developer, no need to. But by learning new languages you improve your skills due to the exposure to new methodologies and idioms. If you've read [The Pragmatic Programmer][2] you know what I mean.

So, why Scala? Because:

+ with Scala you will enter the world of functional programming, without leaving the comfort of Object Oriented development. Functional programming helps scalability and testability. Nowadays, with the rise of multicore processors, using a language that facilitates multithreading by removing the need of locks and threads is a huge win. 
+ Scala syntax and libraries reduce the lines of code you need to write. And LOC have [a direct relation][3] with bugs: less lines, less bugs.
+ Scala runs in the JVM, and it compiles to the same bytecode as Java. This means you can mix Java and Scala code in a program, giving you access to the vast number of existing Java libraries. 
+ Scala removes some of the issues with Java. For example, no more need of Guava collections, Scala gives you what you need.

With great web frameworks like [Play][4] using Scala for web development, good IDE support (like Eclipse or IntelliJ) and knowing you won't leave the JVM, there are no real excuses to not start learning Scala. Besides lazyness, that is.

## But that's for geeks, no one uses it

Not quite true. Yes, early adopters of Scala were geeks, as any early adopter usually is. But nowadays Scala is widely used, and its adoption is growing. You may not see it in "traditional" companies like Accenture. But you know [Twitter][5]? They use Scala. [The Guardian][6]? Scala. [LinkedIn][7]? Scala. [FourSquare][8]. [Nature][9]. Well, [check by yourself][10].

## Ok, but how to start?

First, get your hands on a copy of [Programming in Scala][11]. Read it. And then start coding using Scala as if you coded in Java, don't worry about the new stuff. Just code. Check [Stack Overflow][12] when in doubt. Read other people's code. As time goes you will find better ways to use the new constructs the language provides, and after a few weeks you will fall into "Scala mode".

Recently I was in a Scala talk organised by TypeSafe, the company behind Scala. They mentioned that (in their experience) people starts getting as productive in Scala as in Java after using the language for 4-6 weeks. 

The [official Scala site][13] can help you a bit more.

## What does Scala give me?

I won't list all of Scala capabilities, there is a book you should have bought already for that ;) But if you are curious, some of the features that make Scala great:

+ [Closures][14]: Scala supports closures. In Scala a function is a 1st-level construct and as such it can be assigned, passed as parameter, etc.
+ [Currying][15]
+ [Traits][16], a type of Mixin that makes composition much more powerful than simple Interfaces in Java
+ [Case classes][17], which along the built-in [pattern matching][18] provides a very flexible tool for your algorithms
+ [XML][19] as part of the language (XML as literal)
+ Powerful syntax that allows you to define [operators][20] with names like +, perfect to create [DSL][21] 
+ Parallel Collections, which you can use as normal collections but behind the scenes break themselves automatically into pieces to take advantadge of multiple processors for certain operations (like map, filter, etc).

And [some more][22].

With Scala you can do everything you can do in Java. With less code, less effort, less bugs. And you can do some things Java won't be able to do in the near future.

## To sum it up

I believe Java 7 is not enough. A good step, but not enough. And the reason is that you can use Scala. Java needs to change too much to catch up, even more with the bureaucracy associated to Java processes, and in the meantime Scala will keep evolving.

The Java king is dead, long live the Scala king!


  [2]: http://pragprog.com/book/tpp/the-pragmatic-programmer
  [3]: http://en.wikipedia.org/wiki/Source_lines_of_code#Relation_with_security_faults
  [4]: http://scala.playframework.org/
  [5]: https://twitter.com/
  [6]: http://www.guardian.co.uk/
  [7]: http://press.linkedin.com/about
  [8]: https://foursquare.com/about/new?from=hp
  [9]: http://www.nature.com/
  [10]: http://www.scala-lang.org/node/1658
  [11]: http://www.artima.com/shop/programming_in_scala_2ed
  [12]: http://stackoverflow.com/questions/tagged/scala
  [13]: http://www.scala-lang.org/node/960
  [14]: http://en.wikipedia.org/wiki/Closure_%28computer_science%29
  [15]: http://www.scala-lang.org/node/135
  [16]: http://en.wikipedia.org/wiki/Mixin
  [17]: http://www.scala-lang.org/node/107
  [18]: http://www.scala-lang.org/node/120
  [19]: http://www.scala-lang.org/node/131
  [20]: http://www.scala-lang.org/node/118
  [21]: http://en.wikipedia.org/wiki/Domain-specific_language
  [22]: http://www.scala-lang.org/node/104
