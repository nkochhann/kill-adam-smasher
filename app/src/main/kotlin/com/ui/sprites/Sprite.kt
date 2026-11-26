package com.ui.sprites

import com.googlecode.lanterna.TextColor
import com.ui.tui.Theme
import com.ui.tui.Tui

/** Pixel grid of ARGB ints. Alpha 0 = transparent. */
class Sprite(val width: Int, val height: Int, private val pixels: IntArray) {

    /** Terminal rows needed (2 pixels per row). */
    val cellHeight: Int get() = (height + 1) / 2

    fun pixel(x: Int, y: Int): Int =
        if (x !in 0 until width || y !in 0 until height) 0 else pixels[y * width + x]

    /** Nearest-neighbor resize. The result height is kept even so half-blocks pair up. */
    fun scaled(factor: Double): Sprite {
        if (factor == 1.0) return this
        val nw = Math.round(width * factor).toInt().coerceAtLeast(1)
        var nh = Math.round(height * factor).toInt().coerceAtLeast(2)
        if (nh % 2 != 0) nh++
        val out = IntArray(nw * nh)
        for (y in 0 until nh) {
            val sy = y * height / nh
            for (x in 0 until nw) out[y * nw + x] = pixel(x * width / nw, sy)
        }
        return Sprite(nw, nh, out)
    }

    /** Resolves what to draw in one cell: (char, fg, bg) or null if fully transparent. */
    fun cell(cx: Int, cy: Int): Triple<Char, TextColor, TextColor>? {
        val top = pixel(cx, cy * 2)
        val bottom = pixel(cx, cy * 2 + 1)
        val topOn = alpha(top) > 0
        val bottomOn = alpha(bottom) > 0
        return when {
            !topOn && !bottomOn -> null
            topOn && bottomOn -> Triple('▀', rgb(top), rgb(bottom))
            topOn -> Triple('▀', rgb(top), Theme.PANEL_BG)
            else -> Triple('▄', rgb(bottom), Theme.PANEL_BG)
        }
    }

    /** Draws using half-blocks: top pixel = foreground, bottom pixel = background. */
    fun draw(tui: Tui, x: Int, y: Int) {
        for (cy in 0 until cellHeight) for (cx in 0 until width) {
            val (ch, fg, bg) = cell(cx, cy) ?: continue
            tui.setCell(x + cx, y + cy, ch, fg, bg)
        }
    }

    private fun alpha(argb: Int) = (argb ushr 24) and 0xFF
    private fun rgb(argb: Int) = TextColor.RGB((argb shr 16) and 0xFF, (argb shr 8) and 0xFF, argb and 0xFF)
}
