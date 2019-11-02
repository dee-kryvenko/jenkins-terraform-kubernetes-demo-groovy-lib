package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.util.Container

abstract class AbstractResource implements Serializable {
    private AbstractConfig config
    private AbstractOutput output

    AbstractResource(AbstractConfig config, AbstractOutput output) {
        this.config = config
        this.output = output
    }

    @NonCPS
    List<Container> getPipelineImages() {
        return []
    }
}
