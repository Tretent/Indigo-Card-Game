package indigo

const val INITIAL_NUMBER_OF_CARDS_ON_THE_TABLE = 4
const val CARDS_IN_HAND_PER_DEAL = 6
const val MAX_CARDS = 52

class IndigoGame {
    private var deck = Deck()
    private var cardsOnTable: MutableSet<Card> = deck.getCards(INITIAL_NUMBER_OF_CARDS_ON_THE_TABLE)
    private var humanCardsInHand: MutableSet<Card>
    private var computerCardsInHand: MutableSet<Card>

    init {
        humanCardsInHand = drawCards()
        computerCardsInHand = drawCards()
    }

    private fun drawCards(): MutableSet<Card> = deck.getCards(CARDS_IN_HAND_PER_DEAL)

    fun play() {
        println("Indigo Card Game")
        try {
            while (true) {
                println("Play first?")
                when (readln().trim().lowercase()) {
                    "yes" -> startGame(true)
                    "no" -> startGame(false)
                    "exit" -> println("Game Over").also { return }
                    else -> continue
                }
            }
        } catch (e: Error) {
            if (e.message == "Game over") println("Game Over").also { return }
        }

    }

    private fun startGame(isHumanFirst: Boolean) {
        println("Initial cards on the table: ${cardsOnTable.joinToString(" ")}")

        try {
            while (true)
                if (isHumanFirst) {
                    humanTurn()
                    computerTurn()
                } else {
                    computerTurn()
                    humanTurn()
                }
        } catch (e: Error) {
            throw e
        }
    }

    private fun printCardsOnTable() {
        println()
        println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}")
    }

    private fun humanTurn() {
        printCardsOnTable()
        if (cardsOnTable.size == MAX_CARDS) throw Error("Game over")
        if (humanCardsInHand.size == 0) humanCardsInHand = drawCards()
        println(
            "Cards in hand: ${
                humanCardsInHand.mapIndexed { index, card -> "${index + 1})$card" }.joinToString(" ")
            }"
        )
        try {
            val choice = readCardChosen(humanCardsInHand.size)
            val card = humanCardsInHand.elementAt(choice - 1)
            humanCardsInHand.remove(card)
            cardsOnTable.add(card)
        } catch (e: Error) {
            throw e
        }

    }

    private fun computerTurn() {
        printCardsOnTable()
        if (cardsOnTable.size == MAX_CARDS) throw Error("Game over")
        if (computerCardsInHand.size == 0) computerCardsInHand = drawCards()
        val card = computerCardsInHand.first()

        println("Computer plays $card")

        computerCardsInHand.remove(card)
        cardsOnTable.add(card)
    }

    private fun readCardChosen(maxValue: Int): Int {
        while (true) {
            println("Choose a card to play (1-$maxValue):")
            try {
                val choice = readln().trim().lowercase()
                if (choice == "exit") throw Error("Game over")
                if (choice.toInt() !in 1..maxValue) continue
                else return choice.toInt()
            } catch (e: NumberFormatException) {
                continue
            }
        }
    }
}
