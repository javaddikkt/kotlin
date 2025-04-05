import kotlinx.coroutines.sync.Mutex

class Once {
    private var hadRun = false
    private val mutex = Mutex()

    fun run(block: () -> Unit) {
        if (mutex.tryLock()) {
            if (!hadRun) {
                hadRun = true
                block()
            }
            mutex.unlock()
        }
    }
}
