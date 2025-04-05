import kotlinx.coroutines.*
class SequentialProcessor(private val handler: (String) -> String) : TaskProcessor {
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val thread = newSingleThreadContext("processorThread")

    override suspend fun process(argument: String): String {
        return withContext(thread) {
            handler(argument)
        }
    }

    override fun close() {
        thread.close()
    }
}
