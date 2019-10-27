def call1(type, args) {
    this.metaClass.methodMissing { name, args ->
        println name
        println args[0]
    }
    println type
    println args[0]
    return this
}
