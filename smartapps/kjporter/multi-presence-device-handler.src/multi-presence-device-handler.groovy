/**
 *  Multi Presence Source Handler
 *  This SmartApp integrates multiple presence sensors (GPS + WIFI) into a single virtual presence
 *  Designed for Mom & Dad's house
 */
definition(
    name: "Multi Presence Device Handler",
    namespace: "kjporter",
    author: "Kyle Porter",
    description: "Handles integration of two presence sources into a single presence",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select presence sensors") {
		input "actualPresence", "capability.presenceSensor", title: "Physical presence sensors:", required: true, multiple: true
        input "virtualPresence", "capability.presenceSensor", title: "Virtual presence sensor:", required: true, multiple: false
	}
    section("Select a virtual switch") {
    	input "virtualSwitch", "capability.switch", title: "Virtual switch:", required: false, multiple: false
    }
}

def installed() {
	log.debug "Multi presence handler installed"
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
    log.debug "Initializing presence integration"
	subscribe(actualPresence, "presence", actualPresenceHandler)
    // Lesson learned the hard way... we need to initialize the virtual presence state - otherwise it is null and breaks the handler
    if (virtualPresence.currentValue("presence") == null) {
    	log.debug "Virtual presence state initialized."
    	virtualPresence.present() // Assume present when installed...
    }
}

def actualPresenceHandler(evt) {
	def virtualPresenceState = virtualPresence.currentValue("presence")
    log.debug "Presence status change... now ${evt.value}. Virtual presence state is currently ${virtualPresenceState}."
	if (evt.value == "present") {
    	if (virtualPresenceState == "not present") {
            virtualPresence.present()
            log.debug "Virtual presence turned on (present)"
            if(virtualSwitch) {
				log.debug "Presence switch turned on (present)"
            	virtualSwitch.on()
            }
        }
    }
	else {
    	if (virtualPresenceState == "present") {
        	log.debug "Virtual presence turned off (away)"
            virtualPresence.not_present()
            if(virtualSwitch) {
            	log.debug "Presence switch turned off (away)"
            	virtualSwitch.off()
            }
		}
	}
}