# App scripts

#All elements must be attached to root to be accessible in the app
root = exports ? this

# Some shared constant values
BACKSPACE = 8
ENTER = 13
SPACE = 32
COMMA = 188

#Enables the proper option in the navigation bar menu by reading the id attribute from the header of the section
root.setNavigationBar = (title, menu) ->
    $('#menuHomeArea').removeClass('active')
    $('#menuHowArea').removeClass('active')
    $('#menuTheatreArea').removeClass('active')
    $('#menuContactArea').removeClass('active')
    option = $('#' + menu)
    if option?
        option.addClass('active')
    $('header').attr('id', menu)
    $('#headerBarText').html(title)


#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Below there are core functional scripts, don't change

# Allows $(function() {}); to be used even without Jquery loaded - This code fragments runs the stored calls.
# This is useful so we can push all the $(function() { ...}); calls in templates without having to load jquery at head
window.$.noConflict()
window.$ = window.$.attachReady(jQuery)

# Enable Pjax on all anchors configured to use it
# Anchors with class pjaxLink will do Pjax requests
$('.pjaxLink').pjax('#mainContainer')

# Events to check after ajax load, like social buttons loading
$('#mainContainer').live 'pjax:end', (e, xhr, err) ->
        log("End pjax event fired")
        #console.log("End pjax event fired - end")

$('#mainContainer').live 'pjax:start', (e, xhr, err) ->
        log("Start pjax event fired")


# Prevention of window hijack, run after all jquery scripts
$('html').css 'display': 'none'
if( self == top )
    document.documentElement.style.display = 'block'
else
    top.location = self.location


# Google analytics script, run at the end - Change UA-XXXXX-X to be your site's ID
window._gaq = [['_setAccount', googleAnalyticsCode],['_trackPageview'],['_trackPageLoadTime']];
Modernizr.load load: (if 'https:' == location.protocol then '//ssl' else '//www') + '.google-analytics.com/ga.js'