package indigo

class IndigoGame {
    private val ranks = "A 2 3 4 5 6 7 8 9 10 J Q K".split(" ")
    private val suits = "♦ ♥ ♠ ♣".split(" ")
    private var deck = generateDeck()

    fun play() {
        while (true) {
            println("Choose an action (reset, shuffle, get, exit):")
            when (readln().trim()) {
                "reset" -> reset()
                "shuffle" -> shuffle()
                "get" -> get()
                "exit" -> println("Bye!").also { return }
                else -> println("Wrong action.")
            }
        }
    }

    private fun generateDeck(): MutableSet<String> =
        suits.map { suit -> ranks.map { "$it$suit" } }.flatten().shuffled().toMutableSet()

    private fun reset() {
        deck = generateDeck()
        println("Card deck is reset.")
    }

    private fun shuffle() {
        deck.shuffled()
        println("Card deck is shuffled.")
    }

    private fun get() {
        println("Number of cards:")
        val cardsNumber: Int
        try {
            cardsNumber = readln().toInt()
        } catch (e: NumberFormatException) {
            println("Invalid number of cards.").also { return }
        }
        if (cardsNumber !in (1..52)) println("Invalid number of cards.").also { return }
        if (cardsNumber > deck.size) println("The remaining cards are insufficient to meet the request.").also { return }
        val cards = deck.take(cardsNumber).toSet()
        println(cards.joinToString(" "))
        deck.removeAll(cards)
    }
}
