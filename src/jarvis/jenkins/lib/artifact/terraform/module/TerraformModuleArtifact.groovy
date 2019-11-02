package jarvis.jenkins.lib.artifact.terraform.module

import jarvis.jenkins.lib.artifact.AbstractArtifact
import jarvis.jenkins.lib.util.Template

class TerraformModuleArtifact extends AbstractArtifact implements Serializable {
    TerraformModuleArtifact(TerraformModuleArtifactConfig config, TerraformModuleArtifactOutput output) {
        super(config, output)
    }

    @Override
    String getTestingSteps() {
        return Template.ARTIFACT_TERRAFORM_MODULE_TESTING_STEPS.getTemplate()
    }
}
