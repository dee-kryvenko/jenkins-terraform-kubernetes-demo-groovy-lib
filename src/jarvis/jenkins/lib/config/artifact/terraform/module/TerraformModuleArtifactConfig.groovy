package jarvis.jenkins.lib.config.artifact.terraform.module

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.config.AbstractConfig

class TerraformModuleArtifactConfig extends AbstractConfig implements Serializable {
    private String jarvisTfVersion

    @NonCPS
    String getJarvisTfVersion() {
        return jarvisTfVersion
    }

    void jarvisTfVersion(String jarvisTfVersion) {
        this.jarvisTfVersion = jarvisTfVersion
    }
}
