fun greet(name: String): String {
    return "Hello, $name!"
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(greet(readlnOrNull() ?: "Anonymous"))
        return
    }
    for (s in args) {
        println(greet(s))
    }
}
