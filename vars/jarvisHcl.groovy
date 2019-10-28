import org.codehaus.groovy.runtime.InvokerHelper

def get(context, String resource, type) {
    if (type.size() != 1 || !(type[0] instanceof String)) {
        return "${resource}"(type)
    }
    type = type[0] as String
    def hcl = new Object()
    hcl.metaClass.methodMissing { String name, args ->
        if (args.size() != 1 || !(args[0] instanceof Closure)) {
            throw new MissingMethodException(name, context.class, args)
        }

        Closure body = args[0]
        println "${resource}.${type}.${name}"
        println body
    }
    return hcl
}
