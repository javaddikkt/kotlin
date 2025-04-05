fun fibonacciFor(n: Int): Int {
    var f1 = 0
    var f2 = 1
    var f3: Int
    if (n == 0) {
        return 0
    }
    for (i in 2..n) {
        f3 = f1 + f2
        f1 = f2
        f2 = f3
    }
    return f2
}
