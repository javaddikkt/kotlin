import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun Flow<Cutoff>.resultsFlow(): Flow<Results> {
    return scan(emptyMap<Int, Duration>()) { cur, cutoff ->
        cur + (cutoff.number to cutoff.time)
    }.drop(1).map { Results(it) }
}

fun Flow<Results>.scoreboard(): Flow<Scoreboard> {
    return map { result ->
        result.results
            .entries.sortedBy { it.value }
            .mapIndexed { index, entry ->
                ScoreboardRow(index + 1, entry.key, entry.value)
            }
            .let { Scoreboard(it) }
    }
}

fun main() = runBlocking {
    flow {
        emit(Cutoff(1, 2.minutes))
        emit(Cutoff(3, 4.minutes))
        emit(Cutoff(2, 6.minutes))
    }.resultsFlow().scoreboard().collect { println(it) }
}
