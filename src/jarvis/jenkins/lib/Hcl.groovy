package jarvis.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import jarvis.jenkins.lib.util.JenkinsContext

class Hcl implements Serializable {
    private static class HclHolder implements Serializable {
        static Hcl INSTANCE
    }

    static Hcl it() {
        return HclHolder.INSTANCE
    }

    static void init(context) {
        if (it() == null) {
            HclHolder.INSTANCE = new Hcl(context)
        }
        JenkinsContext.init(context)
    }

    private final def context

    class Resource {
        Resource(Closure body, String resource, String type) {
            this.body = body
            this.config = findClass('config', resource, type)
            this.output = findClass('output', resource, type)
            this.output.wrap(this.config)
            this.resource = findClass(resource, type, this.config, this.output)
        }

        private Closure body
        private AbstractConfig config
        private AbstractOutput output
        private AbstractResource resource

        Closure getBody() {
            return body
        }

        AbstractConfig getConfig() {
            return config
        }

        AbstractOutput getOutput() {
            return output
        }

        AbstractResource getResource() {
            return resource
        }
    }

    private final SortedMap<String, SortedMap<String, SortedMap<String, Resource>>> hcl = new TreeMap<>()

    Hcl(context) {
        this.context = context
    }

    def add(String resource, String type, String name, Closure body) {
        if (!hcl.containsKey(resource)) {
            hcl.put(resource, new TreeMap<>())
        }

        SortedMap<String, SortedMap<String, Resource>> types = hcl[resource]
        if (!types.containsKey(type)) {
            types.put(type, new TreeMap<>())
        }

        SortedMap<String, Resource> resources = types[type]
        if (resources.containsKey(name)) {
            throw new RuntimeException("${resource}.${type}.${name} already defined")
        }

        resources.put(name, new Resource(body, resource, type))
    }

    @NonCPS
    private static <T extends Object> T findClass(String kind = "", String resource, String type, Object... args) {
        String[] split = type.split('_')
        String clazz = "${Hcl.class.getPackage().getName()}.${resource}.${split.join('.')}"
        clazz = "${clazz}.${split.collect() { it.capitalize() }.join()}${resource.capitalize()}${kind.capitalize()}"
        return (T) Class.forName(clazz).newInstance(args as Object[])
    }

    def done() {
        //noinspection GroovyAssignabilityCheck
        SortedMap<String, SortedMap<String, SortedMap<String, AbstractOutput>>> outputs = hcl.collectEntries { kind, types ->
            [(kind): types.collectEntries { type, resources ->
                [(type): resources.collectEntries { name, it ->
                    [(name): it.output]
                }]
            }]
        }

        hcl.each { kind, types ->
            types.each { type, resources ->
                resources.each { name, it ->
                    it.body.setDelegate(it.config)
                    //noinspection UnnecessaryQualifiedReference
                    it.body.setResolveStrategy(Closure.DELEGATE_ONLY)
                    outputs.each { key, value ->
                        it.config.metaClass."${key}" = value
                    }
                    it.body.call()
                }
            }
        }

        JenkinsContext.it().evaluate(new Pipeline(hcl).getJenkinsfile())
    }
}
