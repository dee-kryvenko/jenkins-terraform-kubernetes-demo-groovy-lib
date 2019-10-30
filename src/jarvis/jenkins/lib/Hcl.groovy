package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import io.github.classgraph.AnnotationParameterValueList
import io.github.classgraph.ClassGraph
import jarvis.jenkins.lib.config.AbstractConfig
import jarvis.jenkins.lib.config.Config
import jarvis.jenkins.lib.config.artifact.DockerConfig

class Hcl implements Serializable {
    private static class HclHolder implements Serializable {
        static Hcl INSTANCE
    }

    @NonCPS
    static Hcl it() {
        return HclHolder.INSTANCE
    }

    @NonCPS
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

    @NonCPS
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
    private Class<AbstractConfig> getConfigClass(String resource, String type) {
        context.steps.echo DockerConfig.class.getAnnotation(Config.class).getClass().getName()
        return null
    }

    @NonCPS
    void done() {
        List<AbstractConfig> result = []

        hcl.each { resource, types ->
            types.each { type, names ->
                Class<AbstractConfig> configClass = getConfigClass(resource, type)
                names.each { name, body ->
                    AbstractConfig config = configClass.newInstance()
                    body.setDelegate(config)
                    body.setResolveStrategy(Closure.DELEGATE_FIRST)
                    body.call()
                    result << config
                }
            }
        }

        context.evaluate """
pipeline {
  agent {
    kubernetes {
      defaultContainer 'jnlp'
      yaml '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: maven
    image: maven:alpine
    command:
    - cat
    tty: true
'''
    }
  }
  stages {
    stage('Run maven') {
      steps {
        container('maven') {
          sh 'mvn -version'
          echo "${result.collect() { it.class.getName() }.join(", ")}"
        }
      }
    }
  }
}
"""
    }
}
