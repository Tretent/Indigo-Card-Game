package indigo

data class Card(val rank: Rank, val suit: Suit) {
    override fun toString() = "${rank.symbol}${suit.symbol}"
}
