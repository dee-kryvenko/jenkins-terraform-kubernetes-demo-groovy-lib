def call(String type, String name, Closure body) {
    echo "deployment.${type}.${name}"
}
