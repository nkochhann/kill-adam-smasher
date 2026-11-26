package com.ui.screens

import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.ui.tui.Theme
import com.ui.tui.Tui
import com.ui.tui.formatEddies

/** What the end screen shows about the finished fight. */
data class EndView(val playerName: String, val health: Int, val maxHealth: Int, val eddies: Double)

/** Shared victory / defeat screen: banner, fight summary and a Retry / Main menu choice. */
class EndScreen(private val victory: Boolean) : Screen {
    var view = EndView("", 0, 0, 0.0)

    private val items = listOf("RETRY", "MAIN MENU")
    private var selected = 0

    private val banner = if (victory) {
        listOf(
            "█ █ █▀█ █ █   █   █ █ █▄ █",
            " █  █▄█ █▄█   ▀▄▀▄▀ █ █ ▀█"
        )
    } else {
        listOf(
            "█▀▀ █▀█ █▀▄▀█ █▀▀   █▀█ █ █ █▀▀ █▀█",
            "█▄█ █▀█ █ ▀ █ ██▄   █▄█ ▀▄▀ ██▄ █▀▄"
        )
    }

    override fun render(tui: Tui, tick: Long) {
        val w = tui.width
        val accent = if (victory) Theme.GREEN else Theme.RED
        val accent2 = if (victory) Theme.CYAN else Theme.MAGENTA

        banner.forEachIndexed { i, line -> tui.drawCentered(0, w, 4 + i, line, if (i == 0) accent else accent2) }
        val subtitle = if (victory) "// ADAM SMASHER HAS BEEN TERMINATED //" else "// ${view.playerName} HAS BEEN FLATLINED //"
        tui.drawCentered(0, w, 7, subtitle, Theme.DIM)

        val bw = 34
        val bx = (w - bw) / 2
        tui.drawBox(bx, 9, bw, 6, accent, "RESULT")
        tui.drawCentered(bx, bw, 11, "HP     ${view.health}/${view.maxHealth}", Theme.TEXT, Theme.PANEL_BG)
        tui.drawCentered(bx, bw, 12, "EDDIES ${formatEddies(view.eddies)}", Theme.YELLOW, Theme.PANEL_BG)

        items.forEachIndexed { i, text ->
            val sel = i == selected
            val label = if (sel) "> $text <" else text
            tui.drawCentered(0, w, 17 + i, label, if (sel) Theme.YELLOW else Theme.TEXT)
        }
        tui.drawCentered(0, w, 21, "Up/Down: move   Enter: select", Theme.DIM)
    }

    override fun handleKey(key: KeyStroke): UiEvent = when (key.keyType) {
        KeyType.ArrowUp -> { selected = (selected - 1 + items.size) % items.size; UiEvent.None }
        KeyType.ArrowDown -> { selected = (selected + 1) % items.size; UiEvent.None }
        KeyType.Enter -> {
            val event = if (selected == 0) UiEvent.Retry else UiEvent.Back
            selected = 0
            event
        }
        KeyType.Escape -> { selected = 0; UiEvent.Back }
        else -> UiEvent.None
    }
}
