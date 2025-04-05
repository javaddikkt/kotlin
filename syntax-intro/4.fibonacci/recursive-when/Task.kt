fun fibonacciWhen(n: Int): Int {
    return when (n) {
        0 -> 0
        1 -> 1
        else -> fibonacciWhen(n - 1) + fibonacciWhen(n - 2)
    }
}
