package jarvis.jenkins.lib.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.AbstractArtifactConfig

class DockerArtifactConfig extends AbstractArtifactConfig implements Serializable {
    private String dockerVersion

    DockerArtifactConfig(String name) {
        super(name)
    }

    @NonCPS
    String getDockerVersion() {
        return dockerVersion
    }

    @NonCPS
    void setDockerVersion(String dockerVersion) {
        this.dockerVersion = dockerVersion
    }
}
