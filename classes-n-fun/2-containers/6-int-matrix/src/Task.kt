class IntMatrix(val rows: Int, val columns: Int) {
    init {
        require(rows > 0)
        require(columns > 0)
    }

    private val array: IntArray = IntArray(rows * columns)

    private fun check(x: Int, y: Int) {
        require(!(x < 0 || y < 0 || x >= rows || y >= columns))
    }

    operator fun get(x: Int, y: Int): Int {
        check(x, y)
        return array[x * columns + y]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        check(x, y)
        array[x * columns + y] = value
    }
}

fun main() {
    val matrix = IntMatrix(3, 4)
    println(matrix.rows)
    println(matrix.columns)
    println(matrix[0, 0])
    matrix[2, 3] = 42
    println(matrix[2, 3])
}
