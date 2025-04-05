package turingmachine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.*
import java.io.File

class InvalidMachineDataException
    (message: String) : Exception(message)

class ReadMachine : CliktCommand() {
    private val machineFile: String by argument()
    private val wordFile: String? by argument().optional()
    private val auto: Boolean by option().boolean().default(false)
    private val delayInMillis: Long by option().long().default(500)
    private var startState: String? = null
    private var acceptState: String? = null
    private var rejectState: String? = null
    private var blankLine: String? = null
    private var blank: Char = '_'
    private var transitions: Collection<TransitionFunction> = listOf()
    override fun run() {
        File(machineFile).forEachLine { line ->
            when {
                line.startsWith("start:") -> startState = line.substringAfter("start:").trim()
                line.startsWith("accept:") -> acceptState = line.substringAfter("accept:").trim()
                line.startsWith("reject:") -> rejectState = line.substringAfter("reject:").trim()
                line.startsWith("blank:") -> blankLine = line.substringAfter("blank:").trim()
                "->" in line -> transitions += parseTransition(line)
                else -> throw InvalidMachineDataException("something wrong in description file")
            }
        }

        if (startState == null || acceptState == null || rejectState == null || blankLine == null) {
            throw InvalidMachineDataException("not enough information in description file")
        }

        blank = blankLine?.singleOrNull() ?: throw InvalidMachineDataException("blank element is not a char")

        val word = (
            wordFile?.let { File(it).readText().trim() }
                ?: run {
                    println("Initial word: ")
                    readlnOrNull() ?: ""
                }
            ).replace(blank, BLANK)

        proceed(TuringMachine(startState!!, acceptState!!, rejectState!!, transitions), word)
    }

    private fun parseTransition(line: String): TransitionFunction {
        val parts: List<String> = line.split(" ")
        if (parts.size != 6 || parts[1].length > 1 || parts[4].length > 1 || parts[5].length > 1) {
            throw InvalidMachineDataException("something wrong in description os transitions")
        }
        val move: TapeTransition = (
            when (parts[5].elementAt(0)) {
                '^' -> TapeTransition.Stay
                '<' -> TapeTransition.Left
                '>' -> TapeTransition.Right
                else -> throw InvalidMachineDataException("description of transitions has wrong format")
            }
            )

        val oldSym: Char = parts[1].elementAt(0)
        val newSym: Char = parts[4].elementAt(0)
        return TransitionFunction(
            parts[0],
            if (oldSym == blank) BLANK else oldSym,
            Transition(parts[3], if (newSym == blank) BLANK else newSym, move),
        )
    }

    private fun proceed(machine: TuringMachine, word: String) {
        var snap: TuringMachine.Snapshot = machine.initialSnapshot(word)
        println(snap)
        if (auto) {
            Thread.sleep(delayInMillis)
        } else {
            readln()
        }
        while (true) {
            snap = machine.simulateStep(snap)
            println(snap.toString())
            when (snap.state) {
                acceptState -> {
                    println("accepted")
                    break
                }
                rejectState -> {
                    println("rejected")
                    break
                }
                else -> {
                    if (auto) {
                        Thread.sleep(delayInMillis)
                    } else {
                        readln()
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) = ReadMachine().main(args)
