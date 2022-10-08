package indigo

const val INITIAL_NUMBER_OF_CARDS_ON_THE_TABLE = 4
const val CARDS_IN_HAND_PER_DEAL = 6
val WINNING_CARDS = listOf(Rank.ACE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING)

class IndigoGame {
    private val deck = Deck()
    private val cardsOnTable: MutableSet<Card> = deck.getCards(INITIAL_NUMBER_OF_CARDS_ON_THE_TABLE)
    private var gameStatus = GameStatus.RUNNING
    private val humanPlayer: Player
    private val computerPlayer: Player
    private var lastTaker: Player
    private var firstPlayer: Player

    init {
        humanPlayer = Player("Player", cardsInHand = drawCards())
        computerPlayer = Player("Computer", cardsInHand = drawCards())
        lastTaker = humanPlayer
        firstPlayer = humanPlayer
    }

    private fun drawCards(): MutableSet<Card> = deck.getCards(CARDS_IN_HAND_PER_DEAL)
    private fun isGameOver(): Boolean = gameStatus == GameStatus.GAME_OVER


    fun play() {
        println("Indigo Card Game")
        while (true) {
            if (isGameOver()) println("Game Over").also { return }
            println("Play first?")
            when (readln().trim().lowercase()) {
                "yes" -> startGame(true).also { firstPlayer = humanPlayer; lastTaker = humanPlayer }
                "no" -> startGame(false).also { firstPlayer = computerPlayer; lastTaker = computerPlayer }
                "exit" -> println("Game Over").also { return }
                else -> continue
            }
        }
    }

    private fun startGame(isHumanFirst: Boolean) {
        println("Initial cards on the table: ${cardsOnTable.joinToString(" ")}")

        while (true) {
            if (isHumanFirst) {
                humanTurn()
                if (isGameOver()) return
                computerTurn()
            } else {
                computerTurn()
                if (isGameOver()) return
                humanTurn()
            }
            if (isGameOver()) return
        }
    }

    private fun humanTurn() {
        printCardsOnTable()
        val lastCardOnTable = if (cardsOnTable.isEmpty()) null else cardsOnTable.last()

        if (humanPlayer.cardsInHand.isEmpty()) {
            if (deck.deck.isEmpty()) {
                gameStatus = GameStatus.GAME_OVER
                calculateScore(true)
                printScore()
                return
            }
            humanPlayer.cardsInHand += drawCards()
        }
        println(
            "Cards in hand: ${
                humanPlayer.cardsInHand.mapIndexed { index, card -> "${index + 1})$card" }.joinToString(" ")
            }"
        )
        val choice = readCardChosen(humanPlayer.cardsInHand.size)
        if (isGameOver()) return
        val card = humanPlayer.cardsInHand.elementAt(choice - 1)
        humanPlayer.cardsInHand.remove(card)
        cardsOnTable.add(card)
        if (lastCardOnTable == null) return
        if (playerWonCards(humanPlayer, lastCardOnTable, card)) {
            println("${humanPlayer.name} wins cards")
            printScore()
        }
    }

    private fun computerTurn() {
        printCardsOnTable()
        val lastCardOnTable = if (cardsOnTable.isEmpty()) null else cardsOnTable.last()

        if (computerPlayer.cardsInHand.isEmpty()) {
            if (deck.deck.isEmpty()) {
                gameStatus = GameStatus.GAME_OVER
                calculateScore(true)
                printScore()
                return
            }
            computerPlayer.cardsInHand += drawCards()
        }

        println(computerPlayer.cardsInHand.joinToString(" "))

        val card = computerAI()
        println("Computer plays $card")

        computerPlayer.cardsInHand.remove(card)
        cardsOnTable.add(card)
        if (lastCardOnTable == null) return
        if (playerWonCards(computerPlayer, lastCardOnTable, card)) {
            println("${computerPlayer.name} wins cards")
            printScore()
        }
    }

    private fun computerAI(): Card {
        if (computerPlayer.cardsInHand.size == 1) return computerPlayer.cardsInHand.first()

        return if (cardsOnTable.size == 0) {
            cardWithSameSuiteOrRank(computerPlayer.cardsInHand) ?: computerPlayer.cardsInHand.first()
        } else {
            val candidateCards = candidateCards()

            if (candidateCards.isEmpty()) {
                cardWithSameSuiteOrRank(computerPlayer.cardsInHand) ?: computerPlayer.cardsInHand.first()
            } else if (candidateCards.size == 1) candidateCards.first()
            else {
                cardWithSameSuiteOrRank(candidateCards) ?: candidateCards.first()
            }
        }
    }

    private fun cardWithSameSuiteOrRank(cardSet: MutableSet<Card>): Card? {
        for (cardInHand in cardSet) {
            for (otherCardInHand in cardSet) {
                if (cardInHand == otherCardInHand) continue
                if (cardInHand.suit == otherCardInHand.suit) return cardInHand
            }
        }

        for (cardInHand in cardSet) {
            for (otherCardInHand in cardSet) {
                if (cardInHand == otherCardInHand) continue
                if (cardInHand.rank == otherCardInHand.rank) return cardInHand
            }
        }
        return null
    }

    private fun candidateCards(): MutableSet<Card> =
        computerPlayer.cardsInHand.filter { it.rank == cardsOnTable.last().rank || it.suit == cardsOnTable.last().suit }
            .toMutableSet()

    private fun printCardsOnTable() {
        println()
        println(
            if (cardsOnTable.isEmpty()) "No cards on the table"
            else "${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}"
        )
    }

    private fun printScore() {
        println("Score: ${humanPlayer.name} ${humanPlayer.score} - ${computerPlayer.name} ${computerPlayer.score}")
        println("Cards: ${humanPlayer.name} ${humanPlayer.wonCards.size} - ${computerPlayer.name} ${computerPlayer.wonCards.size}")
    }

    private fun readCardChosen(maxValue: Int): Int {
        while (true) {
            println("Choose a card to play (1-$maxValue):")
            try {
                val choice = readln().trim().lowercase()
                if (choice == "exit") {
                    gameStatus = GameStatus.GAME_OVER
                    return -1
                }
                if (choice.toInt() !in 1..maxValue) continue
                else return choice.toInt()
            } catch (e: NumberFormatException) {
                continue
            }
        }
    }

    private fun playerWonCards(player: Player, topCard: Card, playedCard: Card): Boolean =
        if (topCard.rank == playedCard.rank || topCard.suit == playedCard.suit) {
            player.wonCards += cardsOnTable
            cardsOnTable.clear()
            calculateScore()
            lastTaker = player
            true
        } else false

    private fun calculateScore(lastTurn: Boolean = false) {
        humanPlayer.score = 0
        computerPlayer.score = 0

        if (lastTurn) {
            if (lastTaker == humanPlayer) {
                humanPlayer.wonCards += cardsOnTable
                cardsOnTable.clear()
            } else {
                computerPlayer.wonCards += cardsOnTable
                cardsOnTable.clear()
            }
            if (humanPlayer.wonCards.size > computerPlayer.wonCards.size) humanPlayer.score += 3
            else if (humanPlayer.wonCards.size == computerPlayer.wonCards.size) {
                firstPlayer.score += 3
            } else computerPlayer.score += 3
        }

        humanPlayer.wonCards.forEach { if (it.rank in WINNING_CARDS) humanPlayer.score++ }
        computerPlayer.wonCards.forEach { if (it.rank in WINNING_CARDS) computerPlayer.score++ }
    }
}
