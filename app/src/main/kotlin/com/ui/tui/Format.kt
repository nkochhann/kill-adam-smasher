package com.ui.tui

/** Eddies as text: whole values without decimals ("250"), otherwise one decimal ("12.5"). */
fun formatEddies(amount: Double): String =
    if (amount % 1.0 == 0.0) amount.toLong().toString() else "%.1f".format(java.util.Locale.ROOT, amount)
