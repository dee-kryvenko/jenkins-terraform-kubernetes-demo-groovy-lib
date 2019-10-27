def methodMissing(String name, args) {

  if (args.length == 3 && args[0] instanceof String && args[1] instanceof String && args[2] instanceof Closure) {
    println "${name}.${args[0]}.${args[1]}"
  }

  throw new MissingMethodException(name, this.class, args)
}
