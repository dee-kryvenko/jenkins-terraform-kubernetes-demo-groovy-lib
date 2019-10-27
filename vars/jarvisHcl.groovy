def call1(type, args) {
    println type
    println args[0]
    def fixture = new Object()
    fixture.metaClass.methodMissing { name, args1 ->
        println name
        println args1[0]
    }
    return fixture
}
