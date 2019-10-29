import jarvis.jenkins.lib.Config

def get(context, String resource, type) {
    Config.init(this)

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

        Config.it().add(resource, type, name, body)
    }

    return hcl
}

def done(steps) {
    Config.it().done()
}
