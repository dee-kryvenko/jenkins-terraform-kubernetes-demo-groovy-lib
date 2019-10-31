package jarvis.jenkins.lib

import jarvis.jenkins.lib.artifact.docker.DockerArtifactConfig
import jarvis.jenkins.lib.deployment.terraform.TerraformDeploymentConfig
import jarvis.jenkins.lib.util.JenkinsContext

class Pipeline implements Serializable {
    private SortedMap<String, SortedMap<String, SortedMap<String, Hcl.Resource>>> resources = new TreeMap<>()

    Pipeline(SortedMap<String, SortedMap<String, SortedMap<String, Hcl.Resource>>> resources) {
        this.resources = resources
    }

    String getJenkinsfile() {
        SortedMap<String, SortedMap<String, Hcl.Resource>> artifacts = resources.artifacts
        TerraformDeploymentConfig terraformDeploymentConfig = resources.deployment.terraform.it.config as TerraformDeploymentConfig
        JenkinsContext.it().echo(terraformDeploymentConfig.getJarvisTfVersion())
        DockerArtifactConfig dockerArtifactConfig = resources.artifact.docker.it.config as DockerArtifactConfig
        JenkinsContext.it().echo(dockerArtifactConfig.getDockerVersion())

        String k8s = """{
    kubernetes {
      defaultContainer 'jnlp'
      yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: jnlp
    image: "jenkins/jnlp-slave:3.35-5-alpine"
    tty: true
  - name: dind
    image: "docker:${dockerArtifactConfig.getDockerVersion()}-dind"
    securityContext:
        privileged: true
    env:
        - name: DOCKER_TLS_CERTDIR
          value: ""
  - name: docker
    image: "docker:${dockerArtifactConfig.getDockerVersion()}"
    env:
        - name: DOCKER_HOST
          value: "tcp://localhost:2375"
    command:
    - cat
    tty: true
'''
    }
}
"""
        """
pipeline {
  agent none
  stages {
    stage('Debug1') {
      agent k8s
      steps {
        container("dind") {
          container("docker") {
            sh 'docker ps'
          }
        }
      }
    }
    stage('Debug2') {
      agent ${k8s}
      steps {
        echo 'hi'
      }
    }
  }
}
"""
    }
}
