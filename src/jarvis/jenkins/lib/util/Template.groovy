package jarvis.jenkins.lib.util

import com.cloudbees.groovy.cps.NonCPS

enum Template implements Serializable {
    POD('pod.template'),
    K8S_AGENT('k8sAgent.template'),
    STAGE('stage.template'),
    PIPELINE('pipeline.template'),

    ARTIFACT_DOCKER_TESTING_STEPS('artifact/docker/testing.template'),

    ARTIFACT_TERRAFORM_MODULE_TESTING_STEPS('artifact/terraform/module/testing.template'),

    private String path

    Template(String path) {
        this.path = path
    }

    @NonCPS
    private String getPath() {
        return path
    }

    @NonCPS
    String getTemplate() {
        return JenkinsContext.it().getTemplate(this.getPath())
    }
}