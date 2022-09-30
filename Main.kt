package indigo

fun main() {
    val ranks = "A 2 3 4 5 6 7 8 9 10 J Q K".split(" ")
    val suits = "♦ ♥ ♠ ♣".split(" ")
    println(ranks.joinToString(" "))
    println()
    println(suits.joinToString(" "))
    println()
    suits.forEach { suit -> ranks.forEach { print("$it$suit ") } }
}
