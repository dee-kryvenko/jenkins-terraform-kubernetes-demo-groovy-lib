package jarvis.jenkins.lib.util

import com.cloudbees.groovy.cps.NonCPS

class JenkinsContext implements Serializable {
    private static class VarHolder implements Serializable {
        static final JenkinsContext INSTANCE = new JenkinsContext()
    }

    @NonCPS
    static JenkinsContext it() {
        return VarHolder.INSTANCE
    }

    @NonCPS
    static void init(def context) {
        JenkinsContext instance = it()
        instance.context = context
    }

    private context

    @NonCPS
    def getContext() {
        return context
    }

    @NonCPS
    def getSteps() {
        return this.context.steps
    }

    @NonCPS
    void echo(Object msg) {
        this.getSteps().echo(msg.toString())
    }

    void evaluate(String dsl) {
        this.echo(dsl)
        this.getContext().evaluate(dsl)
    }
}
