package com.ui.screens

import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.ui.tui.Theme
import com.ui.tui.Tui
import com.ui.tui.formatEddies

data class ShopItemView(val name: String, val description: String, val cost: Double)

class ShopScreen(var items: List<ShopItemView>, var eddies: Double) : Screen {
    private var selected = 0

    override fun render(tui: Tui, tick: Long) {
        val w = tui.width
        val bw = minOf(70, w - 2)
        val bx = (w - bw) / 2
        tui.drawBox(bx, 2, bw, items.size + 6, Theme.YELLOW, "BLACK MARKET")
        tui.drawText(bx + 2, 4, "EDDIES: ${formatEddies(eddies)}", Theme.YELLOW, Theme.PANEL_BG)
        items.forEachIndexed { i, item ->
            val y = 6 + i
            val affordable = eddies >= item.cost
            val sel = i == selected
            val color = when {
                !affordable -> Theme.DIM
                sel -> Theme.CYAN
                else -> Theme.TEXT
            }
            val marker = if (sel) "> " else "  "
            tui.drawText(bx + 2, y, marker + item.name, color, Theme.PANEL_BG)
            val cost = "${formatEddies(item.cost)}e"
            tui.drawText(bx + bw - 2 - cost.length, y, cost, if (affordable) Theme.YELLOW else Theme.RED, Theme.PANEL_BG)
        }
        items.getOrNull(selected)?.let { tui.drawText(bx + 2, items.size + 6, it.description.take(bw - 4), Theme.DIM, Theme.PANEL_BG) }
        tui.drawCentered(0, w, items.size + 9, "Up/Down: move  Enter: buy  Esc: back", Theme.DIM)
    }

    override fun handleKey(key: KeyStroke): UiEvent {
        if (items.isEmpty()) return if (key.keyType == KeyType.Escape) UiEvent.Back else UiEvent.None
        return when (key.keyType) {
            KeyType.ArrowUp -> { selected = (selected - 1 + items.size) % items.size; UiEvent.None }
            KeyType.ArrowDown -> { selected = (selected + 1) % items.size; UiEvent.None }
            KeyType.Enter -> if (eddies >= items[selected].cost) UiEvent.Buy(selected) else UiEvent.None
            KeyType.Escape -> UiEvent.Back
            else -> UiEvent.None
        }
    }
}
