def get(context, String resource, type) {
    if (type.size() != 1 || !(type[0] instanceof String)) {
        return context.steps.invokeMethod(resource, type)
    }
    type = type[0] as String
    def hcl = new Object()
    hcl.metaClass.methodMissing { String name, args ->
        if (args.size() != 1 || !(args[0] instanceof Closure)) {
            throw new MissingMethodException(name, context.class, args)
        }

        Closure body = args[0]
        println "${resource}.${type}.${name}"
        println body
    }
    return hcl
}

def done(steps) {
    println "qwe"
    steps.evaluate """
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
        }
      }
    }
  }
}
"""
}
