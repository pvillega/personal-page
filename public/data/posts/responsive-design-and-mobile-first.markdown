# Responsive pages

Mobile browsing is becoming the norm. With the advent of devices like the new iPad or the Nexus 7, predictions that [mobile will surpass desktop in 2014](http://blog.aids.gov/2011/10/mobile-internet-use-surpassing-desktop-internet-use-by-2014.html) seem wrong: it may happen [even earlier](http://www.mobify.com/blog/mobile-web-growing-8-times-faster-than-web/). I find myself, loving desktops as I do, using Internet on my mobile more and more often. That constant access to the net is so handy...

But as a web developer this provides a new challenge: your site needs to work properly both in Mobile and Desktop browsers (and in all the flavours of each: Chrome, Safari, Firefox, IE, etc.). Not so long ago this was solved by having two versions of your site, a mobile one and the classic one, where you redirected the user to the correct one as per their browser (and the mobile site was usually a poorer experience). 
 
Given the new relevance of the mobile versions of the site for the companies, that approach wasn't sustainable. The model has evolved and currently there is a lot of buzz around two concepts that replace it: **Mobile first** and **Responsive Layouts**. 

**Responsive Layout** defines a site whose interface adapts to the size of the view-port of the client. That is, instead of redirecting the user to a version or another according to the browser used, we use CSS media queries to hide or show elements of the page according to the size of the browsing window. 

This means that we can have only one version of our web application, providing the minimal information necessary to a browser with a tiny view-port (usually mobile, but it could be a resized desktop browser) while adding more details for bigger browsers. You can see the concept in action [in this site](http://mattkersley.com/responsive/).

But Responsive Layout doesn't come without its own [set of problems](http://www.netmagazine.com/features/five-responsive-web-design-pitfalls-avoid). The most common one is that small browser windows usually correspond to devices with a poorer network connection when compared to desktops. While hiding elements via CSS simplifies the UX of the site, the elements are still being sent to the device which translates, in many cases, into a very slow experience.

**Mobile First** is a concept that tries to reduce the impact of this issue. Instead of designing a desktop site that hides elements when the browser window is smaller, we design the site for the tiniest device (mobile) and we display more elements if the view-port is bigger. [Progressive enhancement](http://en.wikipedia.org/wiki/Progressive_enhancement) applied.

The combination of both concepts, Responsive Layout and Mobile First, allows us to create our web application once and, with a minimal extra effort, we will be able to provide a better experience to both Mobile and Desktop users. It has the additional benefit of focussing on the minimal functionality required (Mobile First) which may help to reduce clutter on some sites.

## Example

So, how about an example? I've created a very simple web application (in [Play 2.0](http://www.playframework.org/), obviously!) that may help to better understand the concepts behind these two trends. The source code can be found in [Github](https://github.com/pvillega/play2-responsive-sample). Download it and I'll be explaining the code next.

![Desktop view of the example][1]

The application has two content areas. A sidebar in the left that is only visible on a desktop environment and which loads a set of results from Google News, and a main area that contains a bit of text explaining the application plus a set of images obtained from [Gravatar](https://en.gravatar.com/). There is also a colour border around the body, coloured red for a browser with a “desktop” size. Simple, but it should be enough to showcase the ideas behind the concepts discussed above.

The application uses 3 different techniques to manage 3 elements of the application:

* **CSS Media Queries** are used to select the colour of the border around the body of the page. As you resize the browser horizontally the colour should change when you cross one of the preset thresholds. These thresholds are also used to show or hide the sidebar. These changes are dynamically applied when you resize the browser, but on the other hand they don't reduce the amount of content downloaded on page request.

* **Conditional load** for the contents of the sidebar. The list of news items in the sidebar are loaded via a query to Google News. As the sidebar may not be displayed in some browsers, we only execute the query when the viewport is bigger than a given value. This check only happens when we load the page, so if the content wasn't loaded and the browser is then resized so the sidebar becomes visible, the user won't see the news items but some default text that allows the user to access the same content by launching a Google query, so we don't “punish” the user for resizing the browser by removing access to content.

* **Delayed load** for the images in the main area. We could use img tags in the page when rendering it but that would mean that the browser will have to launch multiple requests (one per image) while loading the page. With this technique we mark some anchor tags with metadata and, once the page has been loaded, we inject the img tags into the page. The images will be loaded at that point, but the site itself will be already working and the user can interact with it even in slower connections. We can also modify the size of the image we display according to the viewport so the user downloads a smaller version in devices with potentially worse connections. As with conditional load, this only works on the request, resizing the browser won't launch the script again.

Now that we know what is being done, let's go into the relevant parts of the code.
The application uses 2 templates: main.scala.html provides some common behaviour (like css and javascript files) while index.scala.html contains the relevant code of the demo. The application is using [Twitter Bootstrap](http://twitter.github.com/bootstrap/) for the base layout. Some code (like [Modernizr](http://modernizr.com/) usage) is not explained in this post but the demo contains some comments about it.

To enable the responsive layout from Bootstrap I add in **main.html** the following:

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

This will ensure that the browser doesn't try to fake the viewport size on the request, while still allowing for zooming in mobile devices.

In **index.scala.html** I declare the basic layout with the two areas of the page. I use a fluid layout from Bootstrap plus one of the responsive styles (visible-desktop) which will hide the sidebar if the browser is less than *968px* wide.

    <div class="container-fluid">
        <div class="row-fluid">
               <div class="span2 visible-desktop">
    		        <!-- Sidebar content -->
               </div>
   
               <div class=”span10”>
    	               <!-- Main content -->	
               </div>
        </div>
    </div>

The styles file, **main.css**, contains a set of media queries that will change the border around the body of the page according to the size of the browser:

      /* Landscape phones and down */
      @media (max-width: 480px) {
        body {
            border: 2px magenta solid;
        }
      }
    
      /* Landscape phone to portrait tablet */
      @media (min-width: 480px) and (max-width: 768px) {
        body {
            border: 2px grey solid;
        }
      }
    
      /* Portrait tablet to landscape and desktop */
      @media (min-width: 768px) and (max-width: 979px) {
        body {
            border: 2px blue solid;
        }
      }
    
      /* Desktop */
      @media (min-width: 979px) and (max-width: 1200px) {
         body {
            border: 2px orange solid;
         }
      }
    
      /* Large desktop */
      @media (min-width: 1200px) {
        body {
            border: 2px red solid;
        }
      }

Up to here  there are elements that will be downloaded always, whatever the viewport size, and may change behaviour dynamically as you resize your browser. Many responsive layouts stop here, but if the sidebar contained a lot of data and images, that would penalize mobile users by forcing them to download extra content they'll never see.

To solve this we add a minimal amount of content to the sidebar, so if a user resizes the browser and sees it doesn't get a white gap with no content (progressive enhancement):

    <!--Sidebar content -->
    <h2>Dynamic Sidebar</h2>
    <p>
      This sidebar loads a minimal amount of content by default. If we will display it, some ajax requests will populate it with extra content.     
    </p>
    <hr/>
    <div id="sidebar">
       @* We provide a link to access the data we are not loading in case the user resizes the screen and sees this bar *@
       <a href="http://www.google.com/search?q=responsive+design&amp;amp;tbm=nws">Search Google on Responsive Design</a>
    </div>

and then we add some javascript magic to load the real content dynamically via Conditional Loading. In this example the content loaded is the result of a query to Google News. The details on the retrieval of the data are not relevant to this post. What's relevant is this part of the code (excerpt):

     /* This loads the content of the side bar asynchronously, so we won't do that request if the sidebar won't be shown */
            $(function() {            
                if ($(window).width() >= 979) {
                    searchGoogle('responsive+design');
                }
           });

This fragment will wait until the page has been loaded (equivalent to jquery's `$(document).ready` ) and then check the window size. If the size corresponds to a desktop browser (in which the Bootstrap style visible-desktop will stop hiding the sidebar) then the search is triggered and the content added to the sidebar. Otherwise nothing happens.

This means that for a mobile user there will be no extra work done (besides a simple javascript check) as the information would not be shown, improving the loading time of the page. The fact that the check is done after the main content is ready is also relevant, as the user can start interacting with the main page immediately when in a desktop, and the additional data (which is less relevant to the user) will be loaded in the meantime.

Let's talk now about the images displayed in the main area and Delayed Loading. In this sample  around *500 images* are loaded at once in that section. This could be, in reality, the comments area of a popular blog with lots of comments (and their corresponding images). 

Following the naïve approach the html would be rendered with 500 *img* tags, which would cause the browser to load these images in parallel to the rest of the page, delaying the “ready” status of the page. In Delayed Loading javascript, triggered once the document has loaded, is used to modify the Html DOM. This way the images are loaded later, once the page is ready and functional for the user. This approach also gives extra control on the size of the images loaded.

The relevant code follows:

    /* This loads the avatars asynchronously, so the response time of the page improves as images are loaded by the browser after all content is ready */
           $(function() {
                /* Dynamically select an image size as per viewport size */
                function getAvatarSize() {
                    var size = 100;
                    // we check the viewport width against the same sizes we use in media queries in CSS */
                    var viewport = $(window).width();
                    if(viewport <=  480) {
                        size = 25;
                    } else if(viewport <= 768) {
                        size = 75;
                    } else if(viewport <= 979) {
                        size = 125;
                    } else {
                        size = 175;
                    }
                    return size;
                }
                var avatarSize = getAvatarSize();
    
                /* Load all avatars now */
                $('a[data-gravatar-hash]').prepend(function(index){
                    var hash = $(this).attr('data-gravatar-hash')
                    return '<img alt="Avatar Image" src="http://www.gravatar.com/avatar.php?size=' + avatarSize + '&amp;gravatar_id=' + hash + '"><br/>'
                });
            });

With this code a proper image size (as per the browser size) is selected when the page is ready, and then the img tags are injected into the corresponding placeholders. The result is a delayed loading of secondary content that speeds up the page for the user, providing a better experience specially in slow networks.

To see the impact of these modifications on the page load, see the following network analysis done by Chrome:

![Network analysis by Chrome][2]

As you can see, the page has 2 main blocks of request. The first one corresponds to the basic content of the page (html, css, javascript) and takes *less than 300ms*. At this point the page is ready and the user can interact with it. The second block starts then and corresponds to the delayed requests, taking *additional 315ms*. 

In this simple example we get to the “ready” status **50% faster**. As you can see, this can be a huge benefit for more complex pages, providing the best UX possible to their customers. Worth the extra effort :)

Feel free to add any comments on the subject below!


  [1]: http://perevillega.com/assets/images/posts/responsive-desktop.png
  [2]: http://perevillega.com/assets/images/posts/responsive-network.png