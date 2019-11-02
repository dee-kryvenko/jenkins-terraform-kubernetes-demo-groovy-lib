package jarvis.jenkins.lib.util

import com.cloudbees.groovy.cps.NonCPS
import groovy.text.GStringTemplateEngine

class TemplateEngine implements Serializable {
    private static GStringTemplateEngine engine = new GStringTemplateEngine()

    @NonCPS
    static String render(String template, Map<String, Object> binding = [:]) {
        return engine.createTemplate(template).make(binding).toString()
    }
}
