sealed interface IntResult {
    fun getOrDefault(defaultValue: Int): Int = when (this) {
        is Ok -> value
        is Error -> defaultValue
    }
    fun getOrNull(): Int? = when (this) {
        is Ok -> value
        is Error -> null
    }
    fun getStrict(): Int = when (this) {
        is Ok -> value
        is Error -> throw NoResultProvided(reason)
    }
    data class Ok(val value: Int) : IntResult
    data class Error(val reason: String) : IntResult
}

class NoResultProvided(reason: String) : NoSuchElementException(reason)

fun safeRun(unsafe: () -> Int): IntResult {
    return try {
        IntResult.Ok(unsafe())
    } catch (e: Exception) {
        IntResult.Error(e.message ?: "unknown error")
    }
}
