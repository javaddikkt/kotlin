import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParallelEvaluator {
    suspend fun run(task: Task, n: Int, context: CoroutineContext) {
        withContext(context) {
            launch {
                repeat(n) {
                    try {
                        task.run(it)
                    } catch (e: Exception) {
                        throw TaskEvaluationException(e)
                    }
                }
            }
        }
    }
}
