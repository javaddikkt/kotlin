import kotlin.math.abs

fun isPrime(n: Int): Boolean {
    return when {
        n <= 1 -> false
        else -> {
            var i = 2
            while (i * i <= abs(n)) {
                if (abs(n) % i == 0) {
                    return false
                }
                i++
            }
            return true
        }
    }
}

fun piFunction(x: Double): Int {
    return (1..x.toInt()).count { isPrime(it) }
}
