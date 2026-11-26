package com.ui.screens

import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.ui.tui.Theme
import com.ui.tui.Tui

/** What the weapon selection screen needs to show for one weapon. */
data class WeaponView(
    val name: String,
    val power: String,
    val criticalMultiplier: Int,
    val handleability: Int // 0..5
)

/** Renders a list of weapons as a table and lets the player pick one. */
class WeaponSelectScreen(var weapons: List<WeaponView>) : Screen {
    private var selected = 0

    override fun render(tui: Tui, tick: Long) {
        val w = tui.width
        val bw = minOf(72, w - 2)
        val bx = (w - bw) / 2
        val rows = weapons.size.coerceAtLeast(1)

        tui.drawCentered(0, w, 1, "CHOOSE YOUR WEAPON", Theme.MAGENTA)
        tui.drawBox(bx, 3, bw, rows + 5, Theme.CYAN, "ARSENAL")

        // Column layout, right-aligned numeric columns.
        val handleX = bx + bw - 2 - HANDLE_PIPS
        val critX = handleX - 2 - CRIT_COL
        val powerX = critX - 2 - POWER_COL

        val header = Theme.DIM
        tui.drawText(bx + 4, 5, "NAME", header, Theme.PANEL_BG)
        tui.drawText(powerX, 5, "POWER", header, Theme.PANEL_BG)
        tui.drawText(critX, 5, "CRIT", header, Theme.PANEL_BG)
        tui.drawText(handleX, 5, "HANDLING", header, Theme.PANEL_BG)

        if (weapons.isEmpty()) {
            tui.drawText(bx + 4, 7, "No weapons available.", Theme.DIM, Theme.PANEL_BG)
        }

        weapons.forEachIndexed { i, weapon ->
            val y = 7 + i
            val sel = i == selected
            val nameColor = if (sel) Theme.YELLOW else Theme.TEXT
            tui.drawText(bx + 2, y, if (sel) ">" else " ", Theme.YELLOW, Theme.PANEL_BG)
            tui.drawText(bx + 4, y, weapon.name.take(powerX - bx - 6), nameColor, Theme.PANEL_BG)
            tui.drawText(powerX, y, weapon.power.toString(), Theme.RED, Theme.PANEL_BG)
            tui.drawText(critX, y, "x${weapon.criticalMultiplier}", Theme.MAGENTA, Theme.PANEL_BG)
            drawPips(tui, handleX, y, weapon.handleability)
        }

        tui.drawCentered(0, w, rows + 10, "Up/Down: move   Enter: select   Esc: back", Theme.DIM)
    }

    private fun drawPips(tui: Tui, x: Int, y: Int, value: Int) {
        val filled = value.coerceIn(0, HANDLE_PIPS)
        for (i in 0 until HANDLE_PIPS) {
            if (i < filled) tui.setCell(x + i, y, '●', Theme.GREEN, Theme.PANEL_BG)
            else tui.setCell(x + i, y, '○', Theme.DIM, Theme.PANEL_BG)
        }
    }

    override fun handleKey(key: KeyStroke): UiEvent {
        if (weapons.isEmpty()) return if (key.keyType == KeyType.Escape) UiEvent.Back else UiEvent.None
        return when (key.keyType) {
            KeyType.ArrowUp -> { selected = (selected - 1 + weapons.size) % weapons.size; UiEvent.None }
            KeyType.ArrowDown -> { selected = (selected + 1) % weapons.size; UiEvent.None }
            KeyType.Enter -> UiEvent.SelectWeapon(selected)
            KeyType.Escape -> UiEvent.Back
            else -> UiEvent.None
        }
    }

    private companion object {
        const val HANDLE_PIPS = 5
        const val POWER_COL = 5
        const val CRIT_COL = 4
    }
}
