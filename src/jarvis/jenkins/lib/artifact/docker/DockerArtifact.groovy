package jarvis.jenkins.lib.artifact.docker

import jarvis.jenkins.lib.AbstractConfig
import jarvis.jenkins.lib.AbstractOutput
import jarvis.jenkins.lib.AbstractResource

class DockerArtifact extends AbstractResource implements Serializable {
    DockerArtifact(AbstractConfig config, AbstractOutput output) {
        super(config, output)
    }
}
