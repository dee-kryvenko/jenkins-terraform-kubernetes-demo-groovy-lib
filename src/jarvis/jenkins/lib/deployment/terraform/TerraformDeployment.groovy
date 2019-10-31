package jarvis.jenkins.lib.deployment.terraform

import jarvis.jenkins.lib.deployment.AbstractDeployment

class TerraformDeployment extends AbstractDeployment implements Serializable {
    TerraformDeployment(TerraformDeploymentConfig config, TerraformDeploymentOutput output) {
        super(config, output)
    }
}
