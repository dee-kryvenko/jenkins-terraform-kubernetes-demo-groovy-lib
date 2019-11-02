package jarvis.jenkins.lib.util

import com.cloudbees.groovy.cps.NonCPS

enum Container implements Serializable {
    JNLP,
    DIND,
    DOCKER,

    Container() {
    }

    @NonCPS
    String getTemplate() {
        return JenkinsContext.it().getTemplate("util/containers/${this.name().toLowerCase()}.template")
    }
}
