package jarvis.jenkins.lib.deployment.terraform

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.deployment.AbstractDeploymentConfig

class TerraformDeploymentConfig extends AbstractDeploymentConfig implements Serializable {
    private String jarvisTfVersion

    TerraformDeploymentConfig(String name) {
        super(name)
    }

    @NonCPS
    String getJarvisTfVersion() {
        return jarvisTfVersion
    }

    @NonCPS
    void setJarvisTfVersion(String jarvisTfVersion) {
        this.jarvisTfVersion = jarvisTfVersion
    }
}
