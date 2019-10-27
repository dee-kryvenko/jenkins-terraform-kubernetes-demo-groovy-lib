evaluate(new File("methodMissing.groovy"))

def call(String type, String name, Closure body) {
    echo "deployment.${type}.${name}"
}
