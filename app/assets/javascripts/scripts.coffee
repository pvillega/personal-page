# App scripts

#All elements must be attached to root to be accessible in the app
root = exports ? this

# Some shared constant values
BACKSPACE = 8
ENTER = 13
SPACE = 32
COMMA = 188

#Function that enables teh search by tag in ARchive
# id: id of the element to modify
# values: array with valid suggestions
root.tagSearch = (id, values) ->
    #use bootstrap typeahead element
    $('#'+id).typeahead source: values

    $('#'+id).attr('autocomplete','off')

    $('#'+id).keydown (event) ->
        #avoid enter to propagate
        if event.which is ENTER
            event.preventDefault()

    $('#'+id).keyup (event) ->
        #console.log(event.which)
        #get the type value and hide the non-matching divs

        #clean and trim input
        typed = $(this).val()
        typed = typed.trim()

        if typed isnt ""
            #find all divs which are accordion groups and contain the given string
            $('.accordion-group').hide()
            $('.accordion-group[name*="'+typed+'"]').show()
        else
            $('.accordion-group').show()


#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Below there are core functional scripts, don't change

# Allows $(function() {}); to be used even without Jquery loaded - This code fragments runs the stored calls.
# This is useful so we can push all the $(function() { ...}); calls in templates without having to load jquery at head
window.$.noConflict()
window.$ = window.$.attachReady(jQuery)

# Enable Pjax on all anchors configured to use it
# Anchors with class pjaxLink will do Pjax requests
if $.support.pjax
    $('.pjaxLink').pjax('#mainContent')

# Events to check after ajax load, like social buttons loading
$('#mainContent').live 'pjax:end', (e, xhr, err) ->
        log("End pjax event fired")
        # Reinitialize AddThis if it's inside the loaded fragment
        if window.addthis
            addthis.toolbox $(".addthis_toolbox").get(0)
            addthis.counter $(".addthis_counter").get(0)
        #console.log("End pjax event fired - end")

$('#mainContent').live 'pjax:start', (e, xhr, err) ->
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