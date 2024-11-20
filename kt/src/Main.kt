import java.io.File

enum class Part{
    ONE, TWO
}

enum class Card{
    A, K, Q, J, T, C9, C8, C7, C6, C5, C4, C3, C2
    // by defining cards in descending order we can compare them with .ordinal
}

enum class CardType{
    FIVE, FOUR, FULL, TREE, TWOP, ONEP, HIGH
}

fun detectCardType(cards: List<Card>, part: Part): CardType {
    // cardMap differs between part 1 and part2
    val cardMap = when (part) {
        Part.ONE -> cards
            .groupingBy { it }.eachCount() // idiomatic way in kotlin to count occurrences
            .entries.groupBy(keySelector = { it.value }, valueTransform = { it.key })
        Part.TWO -> {
            // for part TWO, we remove the jokers, then count the other cards as usual
            val jokerCount = cards.count { it == Card.J }
            val otherCards = cards
                .filter { it != Card.J }
                .groupingBy { it }.eachCount() // idiomatic way in kotlin to count occurrences
                .toMutableMap()

                // My Own
                .toList() // convert to list of pairs
                .sortedWith(compareByDescending<Pair<Card, Int>> { it.second } // sort by value descending
                    .thenBy { it.first }) // then sort by key ascending
                .toMutableList() // because now we will update the first one
            if (otherCards.isEmpty()) {
                mapOf(5 to listOf(Card.J))
            } else {
                val highest = otherCards.removeFirst()
                otherCards.add(highest.first to highest.second + jokerCount)
                otherCards
                    .toMap()
                    .entries.groupBy(keySelector = { it.value }, valueTransform = { it.key })
            }

            // ChatGPT
//            repeat(jokerCount) {
//                val maxEntry = otherCards.maxByOrNull { it.value }
//                if (maxEntry != null) {
//                    otherCards[maxEntry.key] = maxEntry.value + 1
//                } else {
//                    // If no cards exist, assign the joker to a new card
//                    otherCards[Card.C2] = 1
//                }
//            }
//
//            otherCards.entries.groupBy(
//                keySelector = { it.value },
//                valueTransform = { it.key }
//            )
        }
    }
    return when {
        5 in cardMap -> CardType.FIVE
        4 in cardMap -> CardType.FOUR
        (3 in cardMap) && (2 in cardMap) -> CardType.FULL
        3 in cardMap -> CardType.TREE
        2 in cardMap && cardMap[2]?.size == 2 -> CardType.TWOP
        2 in cardMap -> CardType.ONEP
        else -> CardType.HIGH
    }
}

data class Hand(
    val cards: List<Card>,
    val bid: Int,
    val part: Part, // matters for the comparison // a bit ugly though to make it a property
    val type: CardType = detectCardType(cards, part)
): Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        val typeComparison = this.type.ordinal.compareTo(other.type.ordinal)
        if (typeComparison != 0) return typeComparison
        repeat(5) { i ->
            val card1 = this.cards[i]
            val card2 = other.cards[i]
            val cardComparison = when (part) {
                Part.ONE -> card1.ordinal.compareTo(card2.ordinal)
                Part.TWO -> {
                    val card1Val = if (card1 == Card.J) Card.entries.size else card1.ordinal
                    val card2Val = if (card2 == Card.J) Card.entries.size else card2.ordinal
                    card1Val.compareTo(card2Val)
                }
            }
            if (cardComparison != 0) return cardComparison
        }
        return 0
    }
}

fun main() {
//    y23d6()
    val data = File("inputs23/7").readText()
//    val data = """32T3K 765
//T55J5 684
//KK677 28
//KTJJT 220
//QQQJA 483
//"""

    val char2Card = mapOf(
        'A' to Card.A,
        'K' to Card.K,
        'Q' to Card.Q,
        'J' to Card.J,
        'T' to Card.T,
        '9' to Card.C9,
        '8' to Card.C8,
        '7' to Card.C7,
        '6' to Card.C6,
        '5' to Card.C5,
        '4' to Card.C4,
        '3' to Card.C3,
        '2' to Card.C2,
    )

    val hands = { part: Part ->
        data
            .split("\n")
            .dropLast(1) // get rid of last "/n"
            //        .asSequence()
            .map { l -> l.split(" ") }
            .map { (cards, bid) ->
                Hand(cards.map { c -> char2Card[c]!! }, bid.toInt(), part)
            }
    }


    val partOneAnswer = hands(Part.ONE)
        .sortedDescending()
        .mapIndexed { i, hand -> hand.bid * (i+1) } // compute rank
        .sum()

    val partTwoAnswer = hands(Part.TWO)
        .sortedDescending()
        .mapIndexed { i, hand -> hand.bid * (i + 1) } // compute rank
        .sum()


    println(partTwoAnswer)

    var hand = Hand(listOf(Card.C2, Card.C2, Card.C3, Card.C4, Card.J), 111, Part.ONE)
    println(hand)
    hand = Hand(listOf(Card.C2, Card.C2, Card.C3, Card.J, Card.J), 111, Part.TWO)
    println(hand)
    hand = Hand(listOf(Card.C2, Card.C2, Card.C3, Card.C4, Card.J), 111, Part.TWO)
    println(hand)

//    println(Card.A.ordinal)

    253982514  // too high
    253300244  // not right
    253499763  // right answer
    254634776
    253616524
    253616524
    253499763
    253499763
    253499763
}