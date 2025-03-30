package com.example.ass2_travaler.componentTool
fun Double.toCurrencyString(currencyCode: String): String {
    val symbol = when (currencyCode) {
        "USD" -> "$"
        "CNY" -> "¥"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "SGD" -> "S$"
        else -> "$"
    }
    return "$symbol${"%.2f".format(this)}"
}
class CurrencyConverter {
    private val rates = mapOf(
        "USD" to 1.0,
        "CNY" to 6.89,
        "EUR" to 0.93,
        "GBP" to 0.82,
        "JPY" to 141.34,
        "SGD" to 1.34
    )

    fun convert(amount: Double, from: String, to: String): Double {
        val baseAmount = amount / rates[from]!!
        return baseAmount * rates[to]!!
    }

}
