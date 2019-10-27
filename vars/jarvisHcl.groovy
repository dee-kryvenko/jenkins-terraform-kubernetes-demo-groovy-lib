def get(resource, type) {
    type = type[0]
    def hcl = new Object()
    hcl.metaClass.methodMissing { name, args ->
        println resource
        println type
        println name
        println args[0]
    }
    return hcl
}
