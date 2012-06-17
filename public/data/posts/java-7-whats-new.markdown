Java 7 will be released [soon](http://www.datamation.com/applications/java-7-release-nears.html), so it's time to check what's new. The official announcement is [here](http://docs.oracle.com/javase/7/docs/technotes/guides/language/enhancements.html). A list of the new additions to our beloved JVM follows: 

+ **Binary literals**: from now on basic numeric types (byte, short, int, long) can be specified as binary numbers using the prefix 0b or 0B
+ **Underscores in literals**: for readability purposes now we can use underscores to mark numbers. You can put them in any place that helps you to read the number better.
+ **Try with resource**: Previously when using some resources like Streams you had to add a finally block just to close it. Now with this new construct Java will close the resource once the try-catch block finishes, either normally or via an exception.
+ **Multiple Exception Caching**: a code saver, from now on the catch clauses accept several exceptions in one clause, avoiding code duplication when several exceptions should be treated the same way. The operator | is used for this.
+ **Better exception analysis on declaration**: this seems a bit confusing but it's simple. If you do a catch Exception, your throws clause in the declaration can still declare very specific exceptions (like IllegalArgumentException). Java will verify that all non-runtime exceptions you generate can only be of the types declared in throws. This allows you to declare better interfaces for error management.
+ **Strings in Switch**: something that was really needed, now you can use Strings as a parameter in the switch statement. Probably is still safer to use enumerations for this, but there are scenarios in which a String-switch is handy.
+ **Type inference**: albeit limited when you compare to Scala's inference, now we have type inference for Generics. This means less typing as you only need to declare the Generic in the left side of the expression.
+ **New compiler warnings on varargs**: when using Generics in varargs parameters the compiler will raise new warnings to notify about the possible issues this may cause.
+ **InvokeDynamic for non-Java languages**: this goes directly into the JVM ecosystem, and it should be a big improvement for languages like Scala. Not something the average Joe programmer will use, but we will feel it on our daily work.
+ **Improvements on NIO library**: never used it too much, but I guess they will be exciting for some people :)
+ **New UI components**: a new component, JLayerPanel, has been added. Not much of a Swing guy here, so not much to say.

I may be missing something, but these are the more relevant ones. Many new things to test this August :) Enjoy the new toys!


