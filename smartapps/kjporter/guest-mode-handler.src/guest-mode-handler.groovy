/**
 *  Guest mode handler
 *  This SmartApp handles switching guest mode and disables a motion sensor when switch is on
 *  Designed for Mom & Dad's house
 */
definition(
    name: "Guest Mode Handler",
    namespace: "kjporter",
    author: "Kyle Porter",
    description: "Handles switching guest mode and disables motion",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Setup guest mode toggling") {
    	input "toggleSwitch", "capability.switch", title: "Which switch toggles guest mode?", required: true, multiple: false
        input "indicatorSwitch", "capability.switch", title: "Which switch indicates guest mode status?", required: true, multiple: false
    } 
    section("Motion sensor configuration") {
    	input "realMotion", "capability.motionSensor", title: "Select motion sensors to be disabled in guest mode:", required: true, multiple: true
    	input "virtualMotion", "capability.motionSensor", title: "Select a virtual motion device as a signal pass-thru:", required: true, multiple: false
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
    log.debug "Initializing guest mode"
	subscribe(toggleSwitch, "switch", modeSwitchHandler)
    subscribe(realMotion, "motion", motionHandler)
}

def modeSwitchHandler(evt) {
	if(evt.value == "on") {
		log.debug "Guest mode switch triggered"
		def switchState = indicatorSwitch.currentState("switch")
		if(switchState.value == "off") {
    		indicatorSwitch.on()
    	}
    	else {
    		indicatorSwitch.off()
    	}
    	toggleSwitch.off()
	}
}

def motionHandler(evt) {
	log.debug "Motion triggered"
	def guestModeState = indicatorSwitch.currentState("switch")
	if (guestModeState.value == "off") {
		if (evt.value == "active") {
			virtualMotion.active()    
    	}
    	else {
    		virtualMotion.inactive()
    	}
	}
}