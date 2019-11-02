package jarvis.jenkins.lib.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.AbstractArtifact
import jarvis.jenkins.lib.util.Container

class DockerArtifact extends AbstractArtifact implements Serializable {
    DockerArtifact(DockerArtifactConfig config, DockerArtifactOutput output) {
        super(config, output)
    }

    @NonCPS
    List<Container> getPipelineImages() {
        return super.getPipelineImages() + [Container.DIND, Container.DOCKER]
    }

    @Override
    List<String> getTestingSteps() {
        return ['''
container("dind-artifact-docker-${config.name}") {
  container("docker-artifact-docker-${config.name}") {
    sh "docker ps"
  }
}
'''.trim()]
    }
}
