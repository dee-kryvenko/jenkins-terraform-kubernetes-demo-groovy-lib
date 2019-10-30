package jarvis.jenkins.lib.config.deployment.terraform

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.config.AbstractConfig

class TerraformDeploymentConfig extends AbstractConfig implements Serializable {
    private String jarvisTfVersion

    @NonCPS
    String getJarvisTfVersion() {
        return jarvisTfVersion
    }

    void jarvisTfVersion(String jarvisTfVersion) {
        this.jarvisTfVersion = jarvisTfVersion
    }
}
