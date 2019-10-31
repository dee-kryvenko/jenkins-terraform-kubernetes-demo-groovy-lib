package jarvis.jenkins.lib.artifact.terraform.module

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.AbstractConfig

class TerraformModuleArtifactConfig extends AbstractConfig implements Serializable {
    private String jarvisTfVersion

    @NonCPS
    String getJarvisTfVersion() {
        return jarvisTfVersion
    }

    @NonCPS
    void setJarvisTfVersion(String jarvisTfVersion) {
        this.jarvisTfVersion = jarvisTfVersion
    }
}
