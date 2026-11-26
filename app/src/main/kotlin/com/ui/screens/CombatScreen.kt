package com.ui.screens

import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.ui.sprites.AdamSmasherSprites
import com.ui.sprites.AdamState
import com.ui.tui.Theme
import com.ui.tui.Tui
import com.ui.tui.formatEddies

/** One side of a D20 exchange: roll + modifier = total. */
data class DiceRollView(val label: String, val roll: Int, val modifier: Int) {
    val total: Int get() = roll + modifier
}

/** Everything the combat screen needs to draw; filled in by the game logic. */
data class CombatView(
    val playerName: String,
    val playerHp: Int,
    val playerMaxHp: Int,
    val eddies: Double,
    val enemyName: String,
    val enemyHp: Int,
    val enemyMaxHp: Int,
    val enemyState: AdamState,
    val attackRoll: DiceRollView?,
    val defenseRoll: DiceRollView?,
    val log: List<String>
)

/**
 * Undertale-style layout, top to bottom: enemy sprite (centered), two dice boxes
 * (attack | defense), hit/blocked result, player status line, a compact combat log,
 * and a row of action buttons.
 */
class CombatScreen(var view: CombatView) : Screen {
    private val idleFrameTicks = 10L
    private val buttons = listOf("FIGHT", "ITEM", "FLEE")
    private var selected = 0

    /** Sprite scale, dice box height, and blank rows between blocks. */
    private class Layout(val scale: Double, val diceH: Int, val gap: Int)

    // Preferred (biggest) first; the last one is the compact fallback for small terminals.
    private val layouts = listOf(
        Layout(2.0, 6, 1),
        Layout(1.5, 6, 1),
        Layout(1.25, 6, 1),
        Layout(1.0, 6, 1),
        Layout(1.0, 4, 0)
    )

    /** Rows used: name + sprite + bar + numbers + dice + result + status + log, plus gaps. */
    private fun contentHeight(l: Layout, spriteRows: Int): Int =
        1 + spriteRows + 2 + l.gap + l.diceH + 1 + l.gap + 1 + l.gap + LOG_H + l.gap

    private companion object {
        const val LOG_H = 4
    }

    override fun render(tui: Tui, tick: Long) {
        val v = view
        val w = tui.width
        val h = tui.height

        // --- Pick the biggest layout that fits, then center the whole block vertically ---
        val state = if (v.enemyState == AdamState.IDLE_1 || v.enemyState == AdamState.IDLE_2) {
            if ((tick / idleFrameTicks) % 2 == 0L) AdamState.IDLE_1 else AdamState.IDLE_2
        } else v.enemyState
        val layout = layouts.firstOrNull { l ->
            val s = AdamSmasherSprites.get(AdamState.IDLE_1, l.scale)
            s.width <= w && contentHeight(l, s.cellHeight) <= h - 3
        } ?: layouts.last()
        val sprite = AdamSmasherSprites.get(state, layout.scale)
        val top = maxOf(0, (h - 3 - contentHeight(layout, sprite.cellHeight)) / 2)

        // --- Enemy: name, centered sprite, HP bar and numbers ---
        val sx = (w - sprite.width) / 2
        tui.drawCentered(0, w, top, "* ${v.enemyName.uppercase().toCharArray().joinToString(" ")} *", Theme.WHITE)
        sprite.draw(tui, sx, top + 1)
        val barY = top + 1 + sprite.cellHeight
        tui.drawBar(sx, barY, sprite.width, v.enemyHp, v.enemyMaxHp, Theme.RED)
        val numY = barY + 1
        tui.drawCentered(0, w, numY, "HP ${v.enemyHp} / ${v.enemyMaxHp}", Theme.TEXT)

        // --- Dice boxes, result, status and log, all centered under Adam ---
        val diceH = layout.diceH
        val logH = LOG_H
        val diceW = minOf(28, (w - 4) / 2)
        val boxGap = 2
        val bw = diceW * 2 + boxGap
        val bx = (w - bw) / 2
        val by = numY + 1 + layout.gap

        // Two separate dice boxes: attack (left) and defense (right)
        val a = v.attackRoll
        val d = v.defenseRoll
        drawDiceBox(tui, bx, by, diceW, diceH, "ATTACK" + (a?.let { " - ${it.label}" } ?: ""), a, Theme.CYAN)
        drawDiceBox(tui, bx + diceW + boxGap, by, diceW, diceH, "DEFENSE" + (d?.let { " - ${it.label}" } ?: ""), d, Theme.RED)

        // Result under the boxes
        if (a != null && d != null) {
            val hit = a.total > d.total
            tui.drawCentered(
                0, w, by + diceH, if (hit) ">> HIT <<" else ">> BLOCKED <<",
                if (hit) Theme.GREEN else Theme.RED
            )
        }

        // --- Status line: NAME  HP [bar] cur / max   EDDIES ---
        val statusY = by + diceH + 1 + layout.gap
        val logY = statusY + 1 + layout.gap
        val ratio = if (v.playerMaxHp <= 0) 0.0 else v.playerHp.toDouble() / v.playerMaxHp
        val hpColor = when {
            ratio > 0.5 -> Theme.GREEN
            ratio > 0.25 -> Theme.YELLOW
            else -> Theme.RED
        }
        // One centered line: NAME  HP [bar] cur / max   EDDIES n
        val name = v.playerName.uppercase().take(10)
        val barW = if (bw >= 50) 20 else 14
        val nums = "${v.playerHp} / ${v.playerMaxHp}"
        val eddies = "EDDIES ${formatEddies(v.eddies)}"
        val totalW = name.length + 2 + 3 + barW + 1 + nums.length + 3 + eddies.length
        var cx = (w - totalW) / 2
        tui.drawText(cx, statusY, name, Theme.WHITE); cx += name.length + 2
        tui.drawText(cx, statusY, "HP", Theme.WHITE); cx += 3
        tui.drawBar(cx, statusY, barW, v.playerHp, v.playerMaxHp, hpColor); cx += barW + 1
        tui.drawText(cx, statusY, nums, Theme.WHITE); cx += nums.length + 3
        tui.drawText(cx, statusY, eddies, Theme.YELLOW)

        // --- Compact combat log: last 2 lines, same width as the dice boxes ---
        tui.drawThinBox(bx, logY, bw, logH, Theme.DIM)
        v.log.takeLast(logH - 2).forEachIndexed { i, line ->
            tui.drawText(bx + 2, logY + 1 + i, "* " + line.take(bw - 6), Theme.WHITE, Theme.PANEL_BG)
        }

        // --- Buttons ---
        val btnW = minOf(16, (w - 10) / buttons.size)
        val gap = 4
        val total = btnW * buttons.size + gap * (buttons.size - 1)
        val startX = (w - total) / 2
        buttons.forEachIndexed { i, label ->
            tui.drawButton(startX + i * (btnW + gap), h - 3, btnW, label, i == selected)
        }
    }

    /** One dice square: colored border + title, then the D20 roll with modifier, then the total. */
    private fun drawDiceBox(
        tui: Tui, x: Int, y: Int, w: Int, h: Int, title: String,
        r: DiceRollView?, color: com.googlecode.lanterna.TextColor
    ) {
        tui.drawThinBox(x, y, w, h, color)
        tui.drawText(x + 2, y, " $title ", color, Theme.PANEL_BG)
        // Content lines are centered vertically inside the box.
        val lines = if (r == null) listOf("..." to Theme.DIM) else {
            val sign = if (r.modifier >= 0) "+" else "-"
            if (h - 2 >= 4) listOf(
                "D20" to Theme.DIM,
                "[ ${r.roll} ]" to Theme.WHITE,
                "$sign ${kotlin.math.abs(r.modifier)} modifier" to Theme.TEXT,
                "TOTAL ${r.total}" to color
            ) else listOf(
                "D20 [${r.roll}] $sign ${kotlin.math.abs(r.modifier)}" to Theme.WHITE,
                "TOTAL ${r.total}" to color
            )
        }
        val startY = y + 1 + ((h - 2) - lines.size) / 2
        lines.forEachIndexed { i, (text, c) -> tui.drawCentered(x, w, startY + i, text, c, Theme.PANEL_BG) }
    }

    override fun handleKey(key: KeyStroke): UiEvent = when (key.keyType) {
        KeyType.ArrowLeft -> { selected = (selected - 1 + buttons.size) % buttons.size; UiEvent.None }
        KeyType.ArrowRight -> { selected = (selected + 1) % buttons.size; UiEvent.None }
        KeyType.Enter -> when (selected) {
            0 -> UiEvent.Fight
            1 -> UiEvent.OpenShop
            else -> UiEvent.Back
        }
        KeyType.Escape -> UiEvent.Back
        else -> UiEvent.None
    }
}
