package com.elab.amortizaplus.domain.util

import java.text.SimpleDateFormat
import java.util.*

object DateProvider {
    fun today(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}