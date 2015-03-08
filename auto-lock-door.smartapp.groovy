/**
 *  Auto Lock Door
 *
 *  Author: Chris Sader (@csader)
 *  Collaborators: @chrisb with modifications from Richard L. Lynch
 *  Date: 2015-03-06
 *  URL: http://www.github.com/smartthings-users/smartapp.auto-lock-door
 *
 * Copyright (C) 2013 Chris Sader.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

definition(
    name: "Auto Lock Door",
    namespace: "rllynch",
    author: "Chris Sader",
    description: "Auto lock door after N minutes",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Solution/doors-locks-active.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Solution/doors-locks-active@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Solution/doors-locks-active@3x.png")

preferences
{
    section("When a door unlocks...") {
        input "lock1", "capability.lock"
    }
    section("Lock it how many minutes later?") {
        input "minutesLater", "number", title: "When?"
    }
}

def installed()
{
    log.debug "Auto Lock Door installed. (URL: http://www.github.com/smartthings-users/smartapp.auto-lock-door)"
    initialize()
}

def updated()
{
    unsubscribe()
    unschedule()
    log.debug "Auto Lock Door updated."
    initialize()
}

def initialize()
{
    log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", doorHandler)
}

def lockDoor()
{
    log.debug "Locking ${lock1} now"
    lock1.lock()
}

def doorHandler(evt)
{
    log.debug "${lock1} is ${evt.value}."

    if (evt.value == "locked") {                  // If the human locks the door then...
        log.debug "Cancelling previous lock task..."
        unschedule( lockDoor )                  // ...we don't need to lock it later.
    }
    else {                                      // If the door is unlocked then...
        def delay = minutesLater * 60          // runIn uses seconds
        log.debug "Locking ${lock1} in ${minutesLater} minutes (${delay}s)."
        runIn( delay, lockDoor )                // ...schedule to lock in x minutes.
    }
}
