package jarvis.jenkins.lib.util

class Proxy extends groovy.util.Proxy {
    Object getProperty(String propertyName) {
        if (this.hasProperty(propertyName)) {
            return super.getProperty(propertyName)
        } else {
            this.getAdaptee().getProperty(propertyName)
        }
    }
}
