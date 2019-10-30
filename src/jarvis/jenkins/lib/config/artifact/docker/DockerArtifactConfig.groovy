package jarvis.jenkins.lib.config.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.config.AbstractConfig

class DockerArtifactConfig extends AbstractConfig implements Serializable {
    private String dockerVersion

    @NonCPS
    String getDockerVersion() {
        return dockerVersion
    }

    @NonCPS
    void setDockerVersion(String dockerVersion) {
        this.dockerVersion = dockerVersion
    }
}
