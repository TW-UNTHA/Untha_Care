package com.utils

object RandomGenerator {

    fun generateRandomString(size: Int): String {
        val source = ('a'..'z') + ('A'..'Z')
        return (source).map { it }.shuffled().subList(0, size).joinToString("")
    }

    fun generateIntBetween0AndTwenty(): Int {
        return (Math.random() * (20 - 0) + 0).toInt()
    }
}