def call1(type, args) {
    def fixture = new Object()
    fixture.metaClass.methodMissing { name, args1 ->
        println type
        println args[0]
        println name
        println args1[0]
    }
    return fixture
}
