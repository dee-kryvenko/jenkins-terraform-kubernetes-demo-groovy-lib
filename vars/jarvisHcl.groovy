def get(context, originalMethodMissing, String resource, type) {
    if (type.size() != 1 || !(type[0] instanceof String)) {
        // context.metaClass.methodMissing = originalMethodMissing
        // context.methodMissing(resource, type)
        originalMethodMissing.invokeMethod(context, resource, type)
        return context
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
