package jarvis.jenkins.lib.artifact.docker

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.AbstractArtifactOutput

class DockerArtifactOutput extends AbstractArtifactOutput implements Serializable {
    private String foo = 'bar'

    @NonCPS
    String getFoo() {
        return foo
    }

    @NonCPS
    void setFoo(String foo) {
        this.foo = foo
    }
}
