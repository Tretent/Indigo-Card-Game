package indigo

class Deck {
    var deck: MutableSet<Card>
        private set

    init {
        deck = generateDeck()
    }

    override fun toString(): String = deck.joinToString(" ")

    private fun generateDeck(): MutableSet<Card> =
        Suit.values().map { suit -> Rank.values().map { rank -> Card(rank, suit) } }.flatten().shuffled().toMutableSet()

    fun getCards(numberOfCards: Int): MutableSet<Card> {
        if (numberOfCards !in (1..52)) throw Error("Invalid number of cards.")
        if (numberOfCards > deck.size) throw Error("The remaining cards are insufficient to meet the request.")
        val cards = deck.take(numberOfCards).toMutableSet()
        deck.removeAll(cards)
        return cards
    }
}
