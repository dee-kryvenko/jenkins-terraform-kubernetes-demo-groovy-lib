@Grab(group='io.github.classgraph', module='classgraph', version='4.8.52')

import jarvis.jenkins.lib.Hcl

def get(context, String resource, type) {
    Hcl.init(this)

    if (type.size() != 1 || !(type[0] instanceof String)) {
        return context.steps.invokeMethod(resource, type)
    }

    type = type[0] as String

    def hcl = new Object()
    hcl.metaClass.methodMissing { String name, args ->
        if (args.size() != 1 || !(args[0] instanceof Closure)) {
            return context.steps.invokeMethod(name, args)
        }

        Closure body = args[0]

        Hcl.it().add(resource, type, name, body)
    }

    return hcl
}

def done(steps) {
    Hcl.it().done()
}
