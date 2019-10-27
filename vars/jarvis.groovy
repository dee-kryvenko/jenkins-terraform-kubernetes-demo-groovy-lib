def call(Closure body) {
    def jarvis = new Object() {
        def version
        def version(String version) {
            this.version = version
        }
    }
    body.setDelegate(jarvis)
    body.setResolveStrategy(Closure.DELEGATE_FIRST)
    body.call()
    library "jarvis@${jarvis.version}"
}
