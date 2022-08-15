package uz.fozilbekimomov.ml_card_scan.utils

import java.text.SimpleDateFormat
import java.util.*


fun String.validCreditCardNumber(): Boolean {
    val ints = IntArray(length)
    for (i in indices) {
        ints[i] = substring(i, i + 1).toInt()
    }
    run {
        var i = ints.size - 2
        while (i >= 0) {
            var j = ints[i]
            j *= 2
            if (j > 9) {
                j = j % 10 + 1
            }
            ints[i] = j
            i -= 2
        }
    }
    var sum = 0
    for (i in ints.indices) {
        sum += ints[i]
    }
    return sum % 10 == 0
}


fun String.validateCardExpiryDate(): Boolean {
    if (isNullOrBlank()) return false
    if (length < 5) return false

    val simpleDateFormat = SimpleDateFormat("MM/yy", Locale.getDefault())
    simpleDateFormat.isLenient = false

    val expiry: Date?
    var expired = false

    try {
        expiry = simpleDateFormat.parse(this)
        expiry?.before(Date())
    } catch (e: Exception) {
        expired = true
    }


    return Regex("(?:0[1-9]|1[0-2])/[0-9]{2}").matches(this) && !expired
}