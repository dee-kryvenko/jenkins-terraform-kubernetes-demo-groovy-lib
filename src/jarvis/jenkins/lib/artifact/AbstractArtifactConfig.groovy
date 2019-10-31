package jarvis.jenkins.lib.artifact

import jarvis.jenkins.lib.AbstractConfig

abstract class AbstractArtifactConfig extends AbstractConfig implements Serializable {
    AbstractArtifactConfig(String name) {
        super(name)
    }
}
