package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS

class Config implements Serializable {
    private static class ConfigHolder implements Serializable {
        static Config INSTANCE
    }

    @NonCPS
    static Config it() {
        return ConfigHolder.INSTANCE
    }

    @NonCPS
    static void init(context) {
        if (it() == null) {
            ConfigHolder.INSTANCE = new Config(context)
        }
    }

    private def context
    private Map<String, Closure> hcl = [:]

    Config(context) {
        this.context = context
    }

    void add(String resource, String type, String name, Closure body) {
        hcl.put(String.join(".", [resource, type, name]), body)
    }

    @Override
    String toString() {
        this.hcl.keySet().join(", ")
    }
}
