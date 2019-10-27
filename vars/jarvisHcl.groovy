def call1(type, args) {
    this.metaClass.methodMissing { name, args1 ->
        println name
        println args1[0]
    }
    println type
    println args[0]
    return this
}
