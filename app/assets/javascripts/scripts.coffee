# App scripts

#All elements must be attached to root to be accessible in the app
root = exports ? this

# Some shared constant values
BACKSPACE = 8
ENTER = 13
SPACE = 32
COMMA = 188

#Function that enables the search by tag in Archive
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

# Prevention of window hijack, run after all jquery scripts
$('html').css 'display': 'none'
if( self == top )
    document.documentElement.style.display = 'block'
else
    top.location = self.location

# Bootstrap CSS Fallback, uses a div with bootstrap modal styles that should not be visible if bootstrap has loaded properly.
if $('.modal.hide').is(':visible')
    $('<link rel="stylesheet" type="text/css" href="/assets/stylesheets/bootstrap.221.min.css" />').prependTo('head')
    $('<link rel="stylesheet" type="text/css" href="/assets/stylesheets/bootstrap-responsive.221.min.css" />').prependTo('head')


