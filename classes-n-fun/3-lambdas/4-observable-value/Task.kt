interface Value<T> {
    val value: T
    fun observe(observer: (T) -> Unit): Cancellation
}

interface Cancellation {
    fun cancel()
}

class MutableValue<T>(initial: T) : Value<T> {
    private val observers = mutableListOf<(T) -> Unit>()
    private var mutValue: T = initial
    override var value: T
        get() = mutValue
        set(newValue) {
            mutValue = newValue
            notify(newValue)
        }

    override fun observe(observer: (T) -> Unit): Cancellation {
        observers.add(observer)
        observer(mutValue)
        return object : Cancellation {
            override fun cancel() {
                observers.remove(observer)
            }
        }
    }

    private fun notify(newValue: T) {
        observers.forEach { it(newValue) }
    }
}
