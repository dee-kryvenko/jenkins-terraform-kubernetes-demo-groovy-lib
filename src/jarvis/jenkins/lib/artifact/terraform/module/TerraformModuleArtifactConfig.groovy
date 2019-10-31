package jarvis.jenkins.lib.artifact.terraform.module

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.artifact.AbstractArtifactConfig

class TerraformModuleArtifactConfig extends AbstractArtifactConfig implements Serializable {
    private String jarvisTfVersion

    TerraformModuleArtifactConfig(String name) {
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
