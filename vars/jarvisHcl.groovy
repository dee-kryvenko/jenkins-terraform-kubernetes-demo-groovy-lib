def call1(type, args) {
    println type
    println args[0]
    return new Object() {
        def methodMissing(name, args1) {
            println name
            println args1[0]
        }
    }
}
