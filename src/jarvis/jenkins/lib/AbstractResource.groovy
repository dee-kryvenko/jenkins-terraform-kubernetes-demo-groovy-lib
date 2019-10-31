package jarvis.jenkins.lib

class AbstractResource implements Serializable {
    private AbstractConfig config
    private AbstractOutput output

    AbstractResource(AbstractConfig config, AbstractOutput output) {
        this.config = config
        this.output = output
    }
}
