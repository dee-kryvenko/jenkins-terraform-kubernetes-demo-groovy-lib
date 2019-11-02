package jarvis.jenkins.lib.util

import com.cloudbees.groovy.cps.NonCPS

enum Template implements Serializable {
    POD('pod.template'),
    K8S_AGENT('k8sAgent.template'),
    STAGE('stage.template'),
    PIPELINE('pipeline.template'),

    private String path

    Template(String path) {
        this.path = path
    }

    @NonCPS
    String getPath() {
        return path
    }
}