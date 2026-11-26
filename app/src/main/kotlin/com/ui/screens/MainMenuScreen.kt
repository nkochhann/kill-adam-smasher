package com.ui.screens

import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.ui.tui.Theme
import com.ui.tui.Tui

class MainMenuScreen : Screen {
    private val items = listOf("START FIGHT", "QUIT")
    private var selected = 0

    private val title = listOf(
        "█▄▀ █ █   █   ▄▀█ █▀▄ ▄▀█ █▀▄▀█",
        "█ █ █ █▄▄ █▄▄ █▀█ █▄▀ █▀█ █ ▀ █",
        "  ▄▀▀ █▀▄▀█ ▄▀█ █▀ █ █ █▀▀ █▀█  ",
        "  ▄██ █ ▀ █ █▀█ ▄█ █▀█ ██▄ █▀▄  "
    )

    override fun render(tui: Tui, tick: Long) {
        val w = tui.width
        title.forEachIndexed { i, line ->
            val color = if (i < 2) Theme.CYAN else Theme.MAGENTA
            tui.drawCentered(0, w, 4 + i, line, color)
        }
        tui.drawCentered(0, w, 9, "// terminal auto-battler //", Theme.DIM)

        val bw = 30
        val bx = (w - bw) / 2
        tui.drawBox(bx, 12, bw, items.size + 4, Theme.CYAN, "MENU")
        items.forEachIndexed { i, text ->
            val sel = i == selected
            val label = if (sel) "> $text <" else text
            tui.drawCentered(bx, bw, 14 + i, label, if (sel) Theme.YELLOW else Theme.TEXT, com.ui.tui.Theme.PANEL_BG)
        }
        tui.drawCentered(0, w, 20, "Up/Down: move   Enter: select", Theme.DIM)
    }

    override fun handleKey(key: KeyStroke): UiEvent = when (key.keyType) {
        KeyType.ArrowUp -> { selected = (selected - 1 + items.size) % items.size; UiEvent.None }
        KeyType.ArrowDown -> { selected = (selected + 1) % items.size; UiEvent.None }
        KeyType.Enter -> if (selected == 0) UiEvent.Start else UiEvent.Quit
        KeyType.Escape -> UiEvent.Quit
        else -> UiEvent.None
    }
}
