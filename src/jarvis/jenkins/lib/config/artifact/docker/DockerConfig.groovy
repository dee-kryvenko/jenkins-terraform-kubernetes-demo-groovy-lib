package jarvis.jenkins.lib.config.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.config.AbstractConfig

class DockerConfig extends AbstractConfig implements Serializable {
    private String dockerVersion

    @NonCPS
    String getDockerVersion() {
        return dockerVersion
    }

    void dockerVersion(String dockerVersion) {
        this.dockerVersion = dockerVersion
    }
}
