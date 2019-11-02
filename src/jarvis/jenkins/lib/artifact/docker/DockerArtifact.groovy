package jarvis.jenkins.lib.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.AbstractArtifact
import jarvis.jenkins.lib.util.Container
import jarvis.jenkins.lib.util.Template

class DockerArtifact extends AbstractArtifact implements Serializable {
    DockerArtifact(DockerArtifactConfig config, DockerArtifactOutput output) {
        super(config, output)
    }

    @NonCPS
    List<Container> getPipelineImages() {
        return super.getPipelineImages() + [Container.DIND, Container.DOCKER]
    }

    @Override
    String getTestingSteps() {
        return Template.ARTIFACT_DOCKER_TESTING_STEPS.getTemplate()
    }
}
