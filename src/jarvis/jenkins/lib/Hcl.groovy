package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.config.AbstractConfig
import jarvis.jenkins.lib.config.artifact.docker.DockerArtifactConfig
import jarvis.jenkins.lib.config.deployment.terraform.TerraformDeploymentConfig

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
    private final Map<String, Map<String, Map<String, AbstractConfig>>> hcl = [:]

    Hcl(context) {
        this.context = context
    }

    def add(String resource, String type, String name, Closure body) {
        if (!hcl.containsKey(resource)) {
            hcl.put(resource, [:])
        }

        Map<String, Map<String, AbstractConfig>> types = hcl[resource]
        if (!types.containsKey(type)) {
            types.put(type, [:])
        }

        Map<String, AbstractConfig> resources = types[type]
        if (resources.containsKey(name)) {
            throw new RuntimeException("${resource}.${type}.${name} already defined")
        }

        AbstractConfig config = findClass('config', resource, type)
//        body = body.clone()
//        body = body.rehydrate(body.getDelegate(), body.getOwner(), body.getThisObject())
//        body.setResolveStrategy(Closure.DELEGATE_FIRST)
        body.setDelegate(config)
        body.setResolveStrategy(Closure.DELEGATE_ONLY)
        try {
            hcl.each { key, value ->
                context.steps.echo "${resource}.${type}.${name} << ${key}: ${value}"
                body."${key}" = value
            }
        } catch (Throwable e) {
            context.steps.echo ">>> ${e.getMessage()}"
        }
        context.steps.echo "about to call"
        body.call()

        resources.put(name, config)

        return context
    }

    @NonCPS
    private static <T extends Object> T findClass(String kind, String resource, String type, Object... args) {
        String[] split = type.split('_')
        String clazz = "${Hcl.class.getPackage().getName()}.${kind}.${resource}.${split.join('.')}"
        clazz = "${clazz}.${split.collect() { it.capitalize() }.join()}${resource.capitalize()}${kind.capitalize()}"
        return (T) Class.forName(clazz).newInstance(args as Object[])
    }

    def done() {
        hcl.artifact.each { type, artifact ->
            artifact.each { name, config ->
                context.steps.echo "${type}.${name}.${config.toString()}"
            }
        }

        TerraformDeploymentConfig terraformDeploymentConfig = hcl.deployment.terraform.it as TerraformDeploymentConfig
        context.steps.echo "jarvisTfVersion = ${terraformDeploymentConfig.jarvisTfVersion}"

        DockerArtifactConfig dockerArtifactConfig = hcl.artifact.docker.it as DockerArtifactConfig
        context.steps.echo "dockerVersion = ${dockerArtifactConfig.dockerVersion}"

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

        return context
    }
}
