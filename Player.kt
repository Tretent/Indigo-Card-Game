package indigo

data class Player(
    val name: String,
    var score: Int = 0,
    val cardsInHand: MutableSet<Card> = mutableSetOf(),
    val wonCards: MutableSet<Card> = mutableSetOf(),
)
