package jarvis.jenkins.lib.deployment

import jarvis.jenkins.lib.AbstractResource

abstract class AbstractDeployment extends AbstractResource implements Serializable {
    AbstractDeployment(AbstractDeploymentConfig config, AbstractDeploymentOutput output) {
        super(config, output)
    }
}
