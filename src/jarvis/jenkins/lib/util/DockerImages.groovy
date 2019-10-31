package jarvis.jenkins.lib.util

import com.cloudbees.groovy.cps.NonCPS

enum DockerImages implements Serializable {
    JNLP('''
- name: jnlp
  image: "jenkins/jnlp-slave:3.35-5-alpine"
  tty: true
'''),
    DIND('''
- name: dind-${key}
  image: "docker:${config.dockerVersion}-dind"
  securityContext:
    privileged: true
  env:
  - name: DOCKER_TLS_CERTDIR
    value: ""
'''),
    DOCKER('''
- name: docker-${key}
  image: "docker:${config.dockerVersion}"
  env:
  - name: DOCKER_HOST
    value: "tcp://localhost:2375"
  command:
  - cat
  tty: true
'''),

    private String yaml

    DockerImages(String yaml) {
        this.yaml = yaml
    }

    @NonCPS
    String getYaml() {
        return yaml
    }
}