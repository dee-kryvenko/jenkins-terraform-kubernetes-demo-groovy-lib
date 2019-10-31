package jarvis.jenkins.lib.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.AbstractArtifact
import jarvis.jenkins.lib.util.DockerImages

class DockerArtifact extends AbstractArtifact implements Serializable {
    DockerArtifact(DockerArtifactConfig config, DockerArtifactOutput output) {
        super(config, output)
    }

    @NonCPS
    List<DockerImages> getPipelineImages() {
        return super.getPipelineImages() + [DockerImages.DIND, DockerImages.DOCKER]
    }

    @Override
    List<String> getTestingSteps() {
        return ['''
container("dind-artifact-docker-${config.name}") {
  container("docker-artifact-docker-${config.name}") {
    sh "docker ps"
  }
}
''']
    }
}
