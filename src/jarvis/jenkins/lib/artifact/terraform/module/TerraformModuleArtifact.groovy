package jarvis.jenkins.lib.artifact.terraform.module

import jarvis.jenkins.lib.artifact.AbstractArtifact

class TerraformModuleArtifact extends AbstractArtifact implements Serializable {
    TerraformModuleArtifact(TerraformModuleArtifactConfig config, TerraformModuleArtifactOutput output) {
        super(config, output)
    }

    @Override
    List<String> getTestingSteps() {
        return [""]
    }
}
