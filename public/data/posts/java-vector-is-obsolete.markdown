Today I was working with a unit test class for my [Google Summer of Code](https://wiki.duraspace.org/display/GSOC/GSOC10+-+Add+Unit+Testing+to+Dspace) project. Launching the test raised a concurrency exception due to me using [ContiPerf](http://databene.org/contiperf.html) to check the performance of some methods. It's a situation that, given the piece of code tested, would not usually happen.

But seldom doesn't equal never, so I proceeded to hunt the source of the error to fix it. The issue was a List that was being accessed via an iterator. The concrete implementation was an [ArrayList](http://download-llnw.oracle.com/javase/6/docs/api/java/util/ArrayList.html), and I though I had cracked open the issue. I still remember from my [SCJP](http://in.sun.com/training/certification/java/scjp.xml) that [Vector](http://download-llnw.oracle.com/javase/6/docs/api/java/util/Vector.html) is the class to use when you want a List that supports concurrency in Java. 

So I replace it, compile the code, everything seems perfect and... wait a second. Netbeans is marking the line with an exclamation mark, let me check... *Vector is deprecated??* What's going on? 

Yes, after a bit of Goggling it seems that Vector is deprecated. The main reason is that Vector does a synchronization per operation. That means you probably need a lock in the vector itself to avoid other threads to change it, as usually you are more interested in a lock for a series of operations, not in a lock for a specific operation.

Vector had not such good performance in its role, so it has been replaced. By who? Welcome the new kid in the block, [Collection.synchronizedList](http://download.oracle.com/javase/6/docs/api/java/util/Collections.html#synchronizedList%28java.util.List). This is a method that takes a List as argument and will return a List with full synchronization whose data is baked by the list you provided. 

You still need to use a lock if you use an iterator over the list, but for other operations the returned list will be thread safe. 

> **Warning**: use the list returned by the method, not the one you
> provided!

 An example taken from the official JavaDoc: 

    List syncList = Collections.synchronizedList(new ArrayList());
    //any operation using syncList will be thread safe 
    synchronized(syncList) { 
       Iterator i = suncList.iterator(); // Iterator must be in synchronized block
       while (i.hasNext()) foo(i.next()); 
    } 

That's all. If you were using Vector (as I did until now):

* Install [Netbeans](http://netbeans.org) 6.9 so it will warn you the next time 
* Refactor your code. You will benefit from safer and more performant code. 

