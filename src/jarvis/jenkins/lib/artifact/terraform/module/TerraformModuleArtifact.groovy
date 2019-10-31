package jarvis.jenkins.lib.artifact.terraform.module

import jarvis.jenkins.lib.AbstractConfig
import jarvis.jenkins.lib.AbstractOutput
import jarvis.jenkins.lib.AbstractResource

class TerraformModuleArtifact extends AbstractResource implements Serializable {
    TerraformModuleArtifact(AbstractConfig config, AbstractOutput output) {
        super(config, output)
    }
}
