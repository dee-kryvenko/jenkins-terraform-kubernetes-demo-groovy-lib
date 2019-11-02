package jarvis.jenkins.lib

import jarvis.jenkins.lib.artifact.AbstractArtifact
import jarvis.jenkins.lib.artifact.AbstractArtifactConfig
import jarvis.jenkins.lib.util.Container
import jarvis.jenkins.lib.util.JenkinsContext
import jarvis.jenkins.lib.util.Template
import jarvis.jenkins.lib.util.TemplateEngine

class Pipeline implements Serializable {
    private SortedMap<String, SortedMap<String, SortedMap<String, Hcl.Resource>>> resources = new TreeMap<>()

    Pipeline(SortedMap<String, SortedMap<String, SortedMap<String, Hcl.Resource>>> resources) {
        this.resources = resources
    }

    String getJenkinsFile() {
        String pipeline = JenkinsContext.it().getTemplate(Template.PIPELINE)
        String stage = JenkinsContext.it().getTemplate(Template.STAGE)
        String k8sAgent = JenkinsContext.it().getTemplate(Template.K8S_AGENT)
        String pod = JenkinsContext.it().getTemplate(Template.POD)

        List<String> testingContainers = []
        List<String> testingSteps = []
        resources.artifact.each { String type, resources ->
            resources.each { String name, Hcl.Resource resource ->
                AbstractArtifact artifact = resource.resource as AbstractArtifact
                AbstractArtifactConfig config = resource.config as AbstractArtifactConfig
                testingContainers << TemplateEngine.render(artifact.getPipelineImages().collect() { it.getTemplate() }.join('\\n'), [
                        key: "artifact-${type}-${name}",
                        config: config
                ])
                testingSteps.addAll(artifact.getTestingSteps().collect() {
                    TemplateEngine.render(it, [
                            config: config
                    ])
                })
            }
        }
        String testingPod = TemplateEngine.render(pod, [
                containers: [Container.JNLP.getTemplate()] + testingContainers
        ])
        String testingAgent = TemplateEngine.render(k8sAgent, [
                pod: testingPod
        ])
        String testingStage = TemplateEngine.render(stage, [
                stageName: "Testing",
                agent: testingAgent,
                steps: testingSteps
        ])

        return TemplateEngine.render(pipeline, [
                stages: [testingStage]
        ])
    }
}
