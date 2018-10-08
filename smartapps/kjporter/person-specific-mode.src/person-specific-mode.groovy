/**
 *  Person-specific mode and switch
 *  This SmartApp extends the one-person switch app
 *  When someone specific departs and someone else is still present, a mode is changed and a switch can throw
 */
definition(
    name: "Person-specific mode",
    namespace: "kjporter",
    author: "Kyle Porter",
    description: "When a specific person is absent, the mode is switched, and optionally a switch is thrown",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Select the switch to turn on") {
    	input "switches", "capability.switch", title: "Which switch(es)?", required: false, multiple: true
    } 
    section("Select the modes to toggle") {
    	input "modeSelectAlone", "mode", title: "Which mode when alone?", required: true, multiple: false
    	input "modeSelectNotAlone", "mode", title: "Which mode when accompanied?", required: true, multiple: false
}
    section("Select the single presence for On"){
    	input "person", "capability.presenceSensor", title: "Which presence?", required: true
    }
    section("Select the other presences") {
    	input "others", "capability.presenceSensor", title: "Which presences?", required: true, multiple: true
    }
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
    log.debug "here2"
	subscribe(person, "presence", personHandler)
}

def personHandler(evt) {
    log.debug "here1"
	if(evt.value == "present") {
    	    def alone = true
            others.each {alone = alone && it.currentPresence == "not present"}
            log.debug "here"
            if(alone) {
                log.debug "alone"
                switches.on()
				setLocationMode(modeSelectAlone)
            }
            else {
                log.debug "not alone"
                switches.off()
				setLocationMode(modeSelectNotAlone)        
            }
        }
}