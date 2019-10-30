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
        new ClassGraph().addClassLoader(this.getClass().getClassLoader()).whitelistPackages(this.getClass().getPackage().getName()).enableAllInfo().scan().withCloseable { scanResult ->
            context.steps.echo Config.class.getName()
            context.steps.echo scanResult.getClassInfo(DockerConfig.class.getName()).getAnnotationInfo(Config.class.getName()).toString()
            context.steps.echo scanResult.getClassesWithAnnotation().getClassesWithAnnotation(Config.class.getName()).size().toString()
            scanResult.getClassesWithAnnotation(Config.class.getName()).find() {
                context.steps.echo it.toString()
                AnnotationParameterValueList config = it.getAnnotationInfo(Config.class.getName()).getParameterValues()
                context.steps.echo config.toString()
                config.get("resource").equals(resource) && config.get("type").equals(type)
            } as Class<AbstractConfig>
        }
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
