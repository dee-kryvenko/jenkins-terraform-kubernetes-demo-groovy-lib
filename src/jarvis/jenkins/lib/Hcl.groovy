package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.docker.DockerArtifactConfig
import jarvis.jenkins.lib.deployment.terraform.TerraformDeploymentConfig

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

    class Resource {
        Closure body
        AbstractConfig config
        AbstractOutput output
    }

    private final Map<String, Map<String, Map<String, Resource>>> hcl = [:]

    Hcl(context) {
        this.context = context
    }

    def add(String resource, String type, String name, Closure body) {
        if (!hcl.containsKey(resource)) {
            hcl.put(resource, [:])
        }

        Map<String, Map<String, Resource>> types = hcl[resource]
        if (!types.containsKey(type)) {
            types.put(type, [:])
        }

        Map<String, Resource> resources = types[type]
        if (resources.containsKey(name)) {
            throw new RuntimeException("${resource}.${type}.${name} already defined")
        }

        Resource it = new Resource()
        it.body = body
        AbstractConfig config = findClass('config', resource, type)
        it.config = config
        AbstractOutput output = findClass('output', resource, type)
        it.output = output
        resources.put(name, it)
    }

    @NonCPS
    private static <T extends Object> T findClass(String kind, String resource, String type, Object... args) {
        String[] split = type.split('_')
        String clazz = "${Hcl.class.getPackage().getName()}.${resource}.${split.join('.')}"
        clazz = "${clazz}.${split.collect() { it.capitalize() }.join()}${resource.capitalize()}${kind.capitalize()}"
        return (T) Class.forName(clazz).newInstance(args as Object[])
    }

    def done() {
        Map<String, Map<String, Map<String, AbstractOutput>>> outputs = hcl.each { kind, types ->
            types.each { type, resources ->
                resources.each { name, it ->
                    context.steps.echo "${kind}.${type}.${name} = ${it}"

                    return it.output
                }
            }
        }

        hcl.each { kind, types ->
            types.each { type, resources ->
                resources.each { name, it ->
                    context.steps.echo "${kind}.${type}.${name} = ${it}"

                    it.body.setDelegate(it.config)
                    it.body.setResolveStrategy(DELEGATE_ONLY)
                    outputs.each { key, value ->
                        it.config.metaClass."${key}" = value
                    }
                    it.body.call()
                }
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
    }
}
