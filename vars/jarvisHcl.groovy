def call1(resource, type) {
    type = type[0]
    def fixture = new Object()
    fixture.metaClass.methodMissing { name, args ->
        println resource
        println type
        println name
        println args[0]
    }
    return fixture
}
