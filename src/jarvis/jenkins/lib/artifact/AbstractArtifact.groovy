package jarvis.jenkins.lib.artifact

import jarvis.jenkins.lib.AbstractResource

abstract class AbstractArtifact extends AbstractResource implements Serializable {
    AbstractArtifact(AbstractArtifactConfig config, AbstractArtifactOutput output) {
        super(config, output)
    }

    abstract List<String> getTestingSteps()
}
