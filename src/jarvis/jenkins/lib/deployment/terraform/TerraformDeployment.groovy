package jarvis.jenkins.lib.deployment.terraform

import jarvis.jenkins.lib.AbstractConfig
import jarvis.jenkins.lib.AbstractOutput
import jarvis.jenkins.lib.AbstractResource

class TerraformDeployment extends AbstractResource implements Serializable {
    TerraformDeployment(AbstractConfig config, AbstractOutput output) {
        super(config, output)
    }
}
