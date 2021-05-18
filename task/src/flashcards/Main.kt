package flashcards

import java.io.File
import java.io.FileNotFoundException
import kotlin.random.Random

var isExit = false
// data structure for cards on current mem
var memory = mutableMapOf<String, String>()
// data structure for ask function,
// stores the card name for random index
var idx = mutableMapOf<String, Int>()
// data structure to store every user input
var log = ""
var willExport = false
var fileNameExport = ""


fun main(args: Array<String>) {

    log = saveToLog(args.toString(), log)

    if(args.contains("-import")) {
        val indexImport = args.indexOf("-import")
        val fileNameImport = args.get(indexImport+1)

        println("File name:")
        log = saveToLog("File name:", log)
        log = saveToLog(fileNameImport, log)

        try {
            var counter = 0
            val toImport = File(fileNameImport).readLines()
            for (line in toImport) {
                var (key, value, err) = line.split("+")
                val error = err.toInt()
                memory.put(key, value)
                idx.put(key, error)
                counter++
            }
            println(printCounterMessage(counter))
            log = saveToLog(printCounterMessage(counter), log)
        } catch(e: FileNotFoundException) {
            println("File not found.")
            log = saveToLog("File not found.", log)
        }

    }

    if (args.contains("-export")) {
        val indexExport = args.indexOf("-export")
        fileNameExport = args.get(indexExport+1)
        willExport = true
    }

    while(!isExit) {
        println(actionMessage())
        log = saveToLog(actionMessage(), log)

        val action = readLine()!!
        log = saveToLog(action, log)

        when(action) {
            "exit" -> {
                isExit = true

                if (willExport) {
                    var toExport = ""
                    //println("File name:")
                    log = saveToLog("File name:", log)
                    log = saveToLog(fileNameExport, log)
                    var counter = 0
                    for ((key, value) in memory) {
                        val error = idx.getValue(key)
                        toExport += "$key+$value+$error\n"
                        counter++
                    }
                    File(fileNameExport).writeText(toExport)
                    println("$counter cards have been saved.")
                    log = saveToLog("$counter cards have been saved.", log)
                }

                println(exitMessage())
                log = saveToLog(exitMessage(), log)
            }
            "add" -> {
                var isUnique = true // bool to check if the card can be added

                log = saveToLog("The card:", log)
                val cardName = getCardName()
                log = saveToLog(cardName, log)

                // checking if name and definition exist in the memory
                if (memory.containsKey(cardName)) {

                    println(printDuplicateCardName(cardName))
                    log = saveToLog(printDuplicateCardName(cardName), log)

                } else {

                    val cardDefinition = getCardDefinition()
                    log = saveToLog("The definition of the card:", log)
                    log = saveToLog(cardDefinition, log)

                    // check if definition exist in memory
                    if (memory.containsValue(cardDefinition)) {

                        println(printDuplicateCardDefinition(cardDefinition))
                        log = saveToLog(printDuplicateCardDefinition(cardDefinition), log)

                        isUnique = false
                    }

                    if (isUnique) {

                        println(printNewCard(cardName, cardDefinition))
                        log = saveToLog(printNewCard(cardName, cardDefinition), log)

                        memory.put(cardName, cardDefinition)
                        idx.put(cardName, 0)

                    }
                }
            }
            "remove" -> {
                println(getWhichCardMessage())
                log = saveToLog(getWhichCardMessage(), log)

                val toRemove = readLine()!!

                log = saveToLog(toRemove, log)

                // check key
                if (memory.containsKey(toRemove)) {
                    memory.remove(toRemove)
                    idx.remove(toRemove)
                    println(removeMessage())
                    log = saveToLog(removeMessage(), log)
                } else {
                    println(noKeyMessage(toRemove))
                    log = saveToLog(noKeyMessage(toRemove), log)
                }
            }
            "import" -> {
                println("File name:")
                log = saveToLog("File name:", log)
                val fileName = readLine()!!
                log = saveToLog(fileName, log)
                try {
                    var counter = 0
                    val toImport = File(fileName).readLines()
                    for (line in toImport) {
                        var (key, value, err) = line.split("+")
                        val error = err.toInt()
                        memory.put(key, value)
                        idx.put(key, error)
                        counter++
                    }
                    println(printCounterMessage(counter))
                    log = saveToLog(printCounterMessage(counter), log)
                } catch(e: FileNotFoundException) {
                    println("File not found.")
                    log = saveToLog("File not found.", log)
                }

            }
            "export" -> {
                println("File name:")
                log = saveToLog("File name:", log)
                var toExport = ""
                val fileName = readLine()!!
                log = saveToLog(fileName, log)
                var counter = 0
                for ((key, value) in memory) {
                    val error = idx.getValue(key)
                    toExport += "$key+$value+$error\n"
                    counter++
                }
                File(fileName).writeText(toExport)
                println("$counter cards have been saved.")
                log = saveToLog("$counter cards have been saved.", log)
            }
            "ask" -> {
                println(printAskMessage())
                log = saveToLog(printAskMessage(), log)
                val numberToAsk = readLine()!!.toInt()
                log = saveToLog(numberToAsk.toString(), log)

                for (i in 1..numberToAsk) {
                    val randomNumber = Random.nextInt(0, idx.size)
                    var counter =  0
                    var answerKey = ""
                    var answerError = 0
                    for ((key, value) in idx) {
                        if (counter == randomNumber) {
                            answerKey = key
                            answerError = value
                            counter++
                        } else {
                            counter++
                        }
                    }

                    val correctAnswer = memory.getValue(answerKey)

                    println(printQns(answerKey))
                    log = saveToLog(printQns(answerKey), log)

                    val userAnswer = readLine()!!
                    log = saveToLog(userAnswer, log)

                    if (userAnswer == correctAnswer) {
                        println("Correct!")
                        log = saveToLog("Correct!", log)
                    } else {
                        if (memory.containsValue(userAnswer)) {
                            for((k, v) in memory) {
                                if (v == userAnswer) {
                                    println(printAnotherTermCorrect(k, correctAnswer))
                                    log = saveToLog(printAnotherTermCorrect(k, correctAnswer), log)
                                }
                            }
                        } else {
                            println(printWrongAnsMessage(correctAnswer))
                            log = saveToLog(printWrongAnsMessage(correctAnswer), log)
                        }
                        answerError += 1
                        idx.set(answerKey, answerError)
                    }

                }
            }
            "show" -> {
                for ((key, value) in memory) {
                    println("$key $value")
                }
                for (key in idx) {
                    println("$key")
                }
            }
            "log" -> {
                println("File name")
                log = saveToLog("File name:", log)
                val userInput = readLine()!!
                log = saveToLog(userInput, log)
                log = saveToLog("The log has been saved", log)
                File(userInput).writeText(log)
                println("The log has been saved")

            }
            "hardest card" -> {
                val hardestValue = getHardestValue(idx)
                if (hardestValue == 0) {
                    println("There are no cards with errors.")
                    log = saveToLog("There are no cards with errors.", log)
                } else {
                    val numOfHardestCards = getNumOfHardestCards(idx, hardestValue)
                    if (numOfHardestCards > 1) {
                        var allHardestKey = getHardestKey(hardestValue, idx)
                        val str = ". You have $hardestValue errors answering them."
                        allHardestKey += str
                        println(allHardestKey)

                        log = saveToLog(allHardestKey, log)
                    } else {
                        var allHardestKey = getHardestKey(hardestValue, idx)
                        val str = ". You have $hardestValue errors answering it."
                        allHardestKey += str
                        println(allHardestKey)

                        log = saveToLog(allHardestKey, log)
                    }

                }
            }
            "reset stats" -> {
                idx = resetStats(idx)
                log = saveToLog("Card statistics have been reset", log)
            }

        }
    }

}

// function to print the action message
fun actionMessage(): String {
    val str = """
        
        Input the action(add, remove, import, export, ask, exit, log, hardest card, reset stats):
    """.trimIndent()
    return str
}

// function to print the exit message
fun exitMessage(): String {
    return "Bye bye!"
}


// function to get card name from user
fun getCardName(): String {
    println("The card:")
    return readLine()!!
}

// function to get card definiton from user
fun getCardDefinition(): String {
    println("The definition of the card:")
    return readLine()!!
}

// function that takes in a duplicate card name and print the error message
fun printDuplicateCardName(name: String): String {
    val str = """
        The card "$name" already exists.
    """.trimIndent()
    return str
}

// function that takes in a duplicate card definition and print the error message
fun printDuplicateCardDefinition(definition: String): String {
    val str = """
        The definition "$definition" already exists.
    """.trimIndent()
    return str
}

// function to print the newly added card into the mem with message
fun printNewCard(name: String, definition: String): String {
    val str = """
        The pair ("$name":"$definition") has been added.
    """.trimIndent()
    return str
}

// function to print the error message for cannot remove
fun noKeyMessage(name: String): String {
    val str = """
        Can't remove "$name": there is no such card
    """.trimIndent()
    return str
}

// function to print remove message
fun removeMessage(): String {
    return "The card has been removed"
}

// function to print counter message
fun printCounterMessage(num: Int): String {
    return "$num cards have been loaded."
}

// function to print ask message
fun printAskMessage(): String {
    return "How many times to ask?"
}

// function to print the question for ask action
fun printQns(key: String): String {
    val str = """
        Print the definition of "$key":
    """.trimIndent()
    return str
}

// function to print the wrong answer for ask action
fun printWrongAnsMessage(value: String): String {
    val str = """
        Wrong. The right answer is "$value".
    """.trimIndent()
    return str
}

// function to print the definition is correct for other terms
fun printAnotherTermCorrect(key: String, value: String): String {
    val str = """
        Wrong. The right answer is "$value", but your definition is correct for "$key".
    """.trimIndent()
    return str
}

// function to save all input and output to log
fun saveToLog(message: String, log: String): String {
    var newLog = log
    newLog += "$message\n"
    return newLog
}

// function to get which card message
fun getWhichCardMessage(): String {
    return "which card?"
}

// function to get the hardest card
fun getHardestValue(idx: MutableMap<String, Int>): Int {
    var hardestValue = 0

    for ((key, value) in idx) {
        if (value > hardestValue) {
            hardestValue = value
        }
    }
    return hardestValue
}

// function to get hardest keys
fun getHardestKey(v: Int, idx: MutableMap<String, Int>): String {
    var str = ""
    var counter = 0
    for ((key, value) in idx) {
        if (v == value && counter < 1) {
            str += """
                "$key"
            """.trimIndent()
            counter++
        } else if (v == value && counter > 0) {
            str += """
                , "$key"
            """.trimIndent()
            counter++
        }
    }
    if (counter > 1) {
        var anotherStr = "The hardest cards are "
        anotherStr += str
        return anotherStr
    } else {
        var anotherString = "The hardest card is "
        anotherString += str
        return anotherString
    }
}

// function to reset stats
fun resetStats(idx: MutableMap<String, Int>): MutableMap<String, Int> {
    for ((key, value) in idx) {
        idx.set(key, 0)
    }
    println("Card statistics have been reset")

    return idx
}

// function to get number of cards with same hardest value
fun getNumOfHardestCards(idx: MutableMap<String, Int>, hardestValue: Int): Int {
    var count = 0
    for ((key, value) in idx) {
        if (value == hardestValue) {
            count++
        }
    }
    return count
}