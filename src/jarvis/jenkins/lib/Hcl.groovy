package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.config.AbstractConfig
import jarvis.jenkins.lib.config.artifact.docker.DockerConfig

class Hcl implements Serializable {
    private static class HclHolder implements Serializable {
        static Hcl INSTANCE
    }

    static Hcl it() {
        return HclHolder.INSTANCE
    }

    static void init(context) {
        if (it() == null) {
            HclHolder.INSTANCE = new Hcl(context)
        }
    }

    private final def context
    private final Map<String, Map<String, Map<String, Closure>>> hcl = [:]

    Hcl(context) {
        this.context = context
    }

    void add(String resource, String type, String name, Closure body) {
        if (!hcl.containsKey(resource)) {
            hcl.put(resource, [:])
        }

        Map<String, Map<String, Closure>> resourceTypes = hcl[resource]
        if (!resourceTypes.containsKey(type)) {
            resourceTypes.put(type, [:])
        }

        Map<String, Closure> resources = resourceTypes[type]
        if (resources.containsKey(name)) {
            throw new RuntimeException("${resource}.${type}.${name} already defined")
        }
        resources.put(name, body)
    }

    @NonCPS
    private static <T extends Object> T findClass(String kind, String resource, String type, Object... args) {
        String[] split = type.split('-')
        String clazz = "${Hcl.class.getPackage().getName()}.${kind}.${resource}.${split.join('.')}"
        clazz = "${clazz}.${split.collect() { it.capitalize() }.join()}${kind.capitalize()}"
        return (T) Class.forName(clazz).newInstance(args as Object[])
    }

    void done() {
        Map<String, AbstractConfig> result = [:]

        hcl.each { resource, types ->
            types.each { type, names ->
                names.each { name, body ->
                    AbstractConfig config = findClass('config', resource, type)
                    body.setDelegate(config)
                    body.setResolveStrategy(Closure.DELEGATE_FIRST)
                    body.call()
                    result.put([resource, type, name].join("."), config)
                }
            }
        }

        DockerConfig dockerConfig = result["artifact.docker.it"]

        context.evaluate """
pipeline {
  agent {
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
    image: "docker:${dockerConfig.getDockerVersion()}-dind"
    securityContext:
        privileged: true
  - name: docker
    image: "docker:${dockerConfig.getDockerVersion()}"
    env:
        - name: DOCKER_HOST
          value: "tcp://localhost:2375"
    command:
    - cat
    tty: true
'''
    }
  }
  stages {
    stage('Debug') {
      steps {
        container("dind") {
          container("docker") {
            sh 'docker ps'
          }
        }
      }
    }
  }
}
"""
    }
}
