import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

typealias ImageGenerator = (query: String) -> ByteArray

class ImageProcessor(
    parallelism: Int,
    private val requests: ReceiveChannel<String>,
    private val publications: SendChannel<Pair<String, ByteArray>>,
    private val generator: ImageGenerator,
) {
    private var cache = mutableSetOf<String>()
    private val semaphore = Semaphore(parallelism)

    fun run(scope: CoroutineScope) {
        scope.launch {
            requests.consumeEach { request ->
                if (!cache.contains(request)) {
                    cache.add(request)
                    semaphore.withPermit {
                        generator(request).also { publications.send(request to it) }
                    }
                }
            }
        }
    }
}
