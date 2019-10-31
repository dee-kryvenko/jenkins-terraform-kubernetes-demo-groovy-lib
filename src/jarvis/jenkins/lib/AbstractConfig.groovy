package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS

abstract class AbstractConfig implements Serializable {
    private String name

    AbstractConfig(String name) {
        this.name = name
    }

    @NonCPS
    String getName() {
        return name
    }
}
