package jarvis.jenkins.lib.deployment

import jarvis.jenkins.lib.AbstractConfig

abstract class AbstractDeploymentConfig extends AbstractConfig implements Serializable {
    AbstractDeploymentConfig(String name) {
        super(name)
    }
}
