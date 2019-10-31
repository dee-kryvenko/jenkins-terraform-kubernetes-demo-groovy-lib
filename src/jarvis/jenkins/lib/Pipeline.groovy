package jarvis.jenkins.lib

import groovy.text.GStringTemplateEngine
import jarvis.jenkins.lib.artifact.AbstractArtifact
import jarvis.jenkins.lib.artifact.AbstractArtifactConfig
import jarvis.jenkins.lib.util.DockerImages

class Pipeline implements Serializable {
    private SortedMap<String, SortedMap<String, SortedMap<String, Hcl.Resource>>> resources = new TreeMap<>()

    Pipeline(SortedMap<String, SortedMap<String, SortedMap<String, Hcl.Resource>>> resources) {
        this.resources = resources
    }

    String getJenkinsfile() {
        GStringTemplateEngine engine = new GStringTemplateEngine()

        String pipeline = '''
pipeline {
  agent none
  stages {
<%= stages.each { %>
<% out.print it.readLines().collect { line -> "    ${line}" }.join('\\n') %>
<%= } %>
  }
}
'''

        String stage = '''
stage("${stageName}") {
  agent ${agent}
  steps {
<%= steps.each { %>
<% out.print it.readLines().collect { line -> "    ${line}" }.join('\\n') %>
<%= } %>
  }
}
'''

        String k8s = '''kubernetes {
    defaultContainer "jnlp"
    yaml """
      apiVersion: v1
      kind: Pod
      spec:
        containers:
<%= containers.each { %>
  <% out.print it.readLines().collect { line -> "        ${line}" }.join('\\n') %>
<%= } %>
'''

        List<String> testingContainers = []
        List<String> testingSteps = []
        resources.artifact.each { String type, resources ->
            resources.each { String name, Hcl.Resource resource ->
                AbstractArtifact artifact = resource.resource as AbstractArtifact
                AbstractArtifactConfig config = resource.config as AbstractArtifactConfig
                testingContainers.addAll(artifact.getPipelineImages().collect() {
                    engine.createTemplate(it.getYaml()).make([
                            key: "artifact-${type}-${name}",
                            config: config
                    ]).toString()
                })
                testingSteps.addAll(artifact.getTestingSteps().collect() {
                    engine.createTemplate(it).make([
                            config: config
                    ]).toString()
                })
            }
        }
        String testingAgent = engine.createTemplate(k8s).make([
                containers: [DockerImages.JNLP.getYaml()] + testingContainers
        ]).toString()
        String testingStage = engine.createTemplate(stage).make([
                stageName: "Testing",
                agent: testingAgent,
                steps: testingSteps
        ]).toString()

        return engine.createTemplate(pipeline).make([
                stages: [testingStage]
        ]).toString()
    }
}
