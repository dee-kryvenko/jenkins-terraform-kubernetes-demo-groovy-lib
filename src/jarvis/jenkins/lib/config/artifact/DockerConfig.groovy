package jarvis.jenkins.lib.config.artifact

import jarvis.jenkins.lib.config.AbstractConfig
import jarvis.jenkins.lib.config.Config

@Config(resource = "artifact", type = "docker")
class DockerConfig extends AbstractConfig implements Serializable {
}
