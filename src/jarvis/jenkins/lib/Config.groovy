package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS

class Config implements Serializable {
    private static class ConfigHolder implements Serializable {
        static Config INSTANCE
    }

    @NonCPS
    static Config it() {
        return ConfigHolder.INSTANCE
    }

    @NonCPS
    static void init(context) {
        if (it() == null) {
            ConfigHolder.INSTANCE = new Config(context)
        }
    }

    private def context
    private Map<String, Closure> hcl = [:]

    Config(context) {
        this.context = context
    }

    void add(String resource, String type, String name, Closure body) {
        hcl.put(String.join(".", [resource, type, name]), body)
    }

    void done() {
        String result = this.hcl.keySet().join(", ")

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
          echo "${result}"
        }
      }
    }
  }
}
"""
    }
}
