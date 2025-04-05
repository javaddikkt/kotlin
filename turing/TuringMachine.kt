package turingmachine

import kotlin.math.max

class TuringMachine(
    private val startingState: String,
    private val acceptedState: String,
    private val rejectedState: String,
) {
    private var table: MutableMap<Char, HashMap<String, Transition>> = hashMapOf()

    constructor(st: String, ac: String, rj: String, transitions: Collection<TransitionFunction>) : this(st, ac, rj) {
        transitions.forEach { element ->
            table.getOrPut(element.symbol) { hashMapOf() }[element.state] = element.transition
        }
    }

    fun initialSnapshot(input: String): Snapshot {
        return Snapshot(startingState, Tape(input))
    }

    fun simulateStep(snapshot: Snapshot): Snapshot {
        val transition: Transition? = table[snapshot.tape.getChar()]?.get(snapshot.state)
        return when (transition) {
            null -> snapshot.applyTransition(Transition(rejectedState, snapshot.tape.getChar(), TapeTransition.Stay))
            else -> snapshot.applyTransition(transition)
        }
    }

    fun simulate(initialString: String): Sequence<Snapshot> {
        var i = 1
        var sequence: Sequence<Snapshot> = sequenceOf()
        var snapshot = Snapshot(startingState, Tape(initialString))
        sequence += snapshot.copy()
        while (i < 300 && snapshot.state != rejectedState && snapshot.state != acceptedState) {
            i++
            snapshot = simulateStep(snapshot)
            sequence += snapshot.copy()
        }
        return sequence
    }

    class Snapshot(val state: String, val tape: Tape) {
        fun applyTransition(transition: Transition): Snapshot {
            return Snapshot(transition.newState, tape.applyTransition(transition.newSymbol, transition.move))
        }

        override fun equals(other: Any?): Boolean {
            return (other is Snapshot && state == other.state && tape == other.tape)
        }

        override fun toString(): String {
            return "state: $state\n$tape"
        }

        fun copy(): Snapshot {
            return Snapshot(state, tape.copy())
        }

        override fun hashCode(): Int {
            return 31 * state.hashCode() + tape.hashCode()
        }
    }

    class Tape(line: String = "") {
        private var head = 0
        private var start = 0
        private var end = line.length
        val position: Int
            get() {
                return when {
                    head < start -> 0
                    else -> head - start
                }
            }

        private var array: CharArray = line.toCharArray()
        val content: CharArray
            get() {
                return when {
                    array.isEmpty() -> CharArray(1) { BLANK }
                    head < start -> {
                        CharArray(start - head) { BLANK } + array.copyOfRange(start, end)
                    }
                    head >= end -> {
                        array.copyOfRange(start, end) + CharArray(head - end + 1) { BLANK }
                    }
                    else -> array.copyOfRange(start, end)
                }
            }

        fun getChar(): Char {
            return when {
                head < 0 || head >= array.size -> BLANK
                else -> array[head]
            }
        }

        fun applyTransition(char: Char, move: TapeTransition): Tape {
            if (char != BLANK) {
                if (head >= array.size) {
                    array += CharArray(head - array.size + 1) { BLANK }
                    end = array.size
                } else if (head < 0) {
                    array = CharArray(-head) { BLANK } + array
                    end -= head
                    start = 0
                    head = 0
                }
            }

            if (head in array.indices) {
                array[head] = char
            }

            if (array.isNotEmpty()) {
                when (move) {
                    TapeTransition.Left -> {
                        if (head == array.size - 1 && array[head] == BLANK) {
                            end = array.size - 1
                        }
                        head--
                    }

                    TapeTransition.Right -> {
                        if (head == 0 && array[head] == BLANK) {
                            start++
                        }
                        head++
                    }

                    TapeTransition.Stay -> {}
                }
            }
            return this
        }

        fun copy(): Tape {
            val newTape = Tape()
            newTape.head = head
            newTape.array = array.copyOf()
            newTape.start = start
            newTape.end = end
            return newTape
        }

        override fun equals(other: Any?): Boolean {
            return (other is Tape && position == other.position && content.contentEquals(other.content))
        }

        override fun toString(): String {
            return String(content) + "\n" + " ".repeat(max(head - start, 0)) + "^"
        }

        override fun hashCode(): Int {
            return 31 * position + content.contentHashCode()
        }
    }
}
