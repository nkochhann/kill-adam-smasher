package com.ui.tui

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

/** Thin wrapper over Lanterna's Screen with the drawing primitives the game needs. */
class Tui : AutoCloseable {
    private val screen: Screen = DefaultTerminalFactory().createScreen()
    private val gfx = screen.newTextGraphics()

    val width: Int get() = screen.terminalSize.columns
    val height: Int get() = screen.terminalSize.rows

    fun start() {
        screen.startScreen()
        screen.cursorPosition = null
    }

    fun clear() {
        screen.doResizeIfNecessary()
        gfx.backgroundColor = Theme.BG
        gfx.fillRectangle(
            com.googlecode.lanterna.TerminalPosition(0, 0),
            screen.terminalSize,
            ' '
        )
    }

    fun refresh() = screen.refresh()

    /** Non-blocking; returns null when no key is pending. */
    fun readKey(): KeyStroke? = screen.pollInput()

    fun setCell(x: Int, y: Int, ch: Char, fg: TextColor, bg: TextColor = Theme.BG) {
        if (x < 0 || y < 0 || x >= width || y >= height) return
        gfx.foregroundColor = fg
        gfx.backgroundColor = bg
        gfx.setCharacter(x, y, ch)
    }

    fun drawText(x: Int, y: Int, text: String, fg: TextColor = Theme.TEXT, bg: TextColor = Theme.BG) {
        text.forEachIndexed { i, c -> setCell(x + i, y, c, fg, bg) }
    }

    fun drawCentered(x: Int, w: Int, y: Int, text: String, fg: TextColor = Theme.TEXT, bg: TextColor = Theme.BG) {
        drawText(x + (w - text.length) / 2, y, text, fg, bg)
    }

    fun fillRect(x: Int, y: Int, w: Int, h: Int, bg: TextColor) {
        for (j in y until y + h) for (i in x until x + w) setCell(i, j, ' ', Theme.TEXT, bg)
    }

    /** Double-line neon box with an optional title. */
    fun drawBox(x: Int, y: Int, w: Int, h: Int, color: TextColor, title: String? = null) {
        fillRect(x, y, w, h, Theme.PANEL_BG)
        for (i in 1 until w - 1) {
            setCell(x + i, y, '═', color, Theme.PANEL_BG)
            setCell(x + i, y + h - 1, '═', color, Theme.PANEL_BG)
        }
        for (j in 1 until h - 1) {
            setCell(x, y + j, '║', color, Theme.PANEL_BG)
            setCell(x + w - 1, y + j, '║', color, Theme.PANEL_BG)
        }
        setCell(x, y, '╔', color, Theme.PANEL_BG)
        setCell(x + w - 1, y, '╗', color, Theme.PANEL_BG)
        setCell(x, y + h - 1, '╚', color, Theme.PANEL_BG)
        setCell(x + w - 1, y + h - 1, '╝', color, Theme.PANEL_BG)
        if (title != null) drawText(x + 2, y, " $title ", color, Theme.PANEL_BG)
    }

    /** Thin single-line box (Undertale style). */
    fun drawThinBox(x: Int, y: Int, w: Int, h: Int, color: TextColor) {
        fillRect(x, y, w, h, Theme.PANEL_BG)
        for (i in 1 until w - 1) {
            setCell(x + i, y, '─', color, Theme.PANEL_BG)
            setCell(x + i, y + h - 1, '─', color, Theme.PANEL_BG)
        }
        for (j in 1 until h - 1) {
            setCell(x, y + j, '│', color, Theme.PANEL_BG)
            setCell(x + w - 1, y + j, '│', color, Theme.PANEL_BG)
        }
        setCell(x, y, '┌', color, Theme.PANEL_BG)
        setCell(x + w - 1, y, '┐', color, Theme.PANEL_BG)
        setCell(x, y + h - 1, '└', color, Theme.PANEL_BG)
        setCell(x + w - 1, y + h - 1, '┘', color, Theme.PANEL_BG)
    }

    /** 3-row button: orange border normally, yellow when selected. */
    fun drawButton(x: Int, y: Int, w: Int, label: String, selected: Boolean) {
        val color = if (selected) Theme.YELLOW else Theme.ORANGE
        drawThinBox(x, y, w, 3, color)
        drawCentered(x, w, y + 1, label, color, Theme.PANEL_BG)
    }

    fun drawBar(x: Int, y: Int, w: Int, value: Int, max: Int, color: TextColor) {
        val filled = if (max <= 0) 0 else (value.coerceIn(0, max) * w) / max
        for (i in 0 until w) {
            if (i < filled) setCell(x + i, y, '█', color, Theme.PANEL_BG)
            else setCell(x + i, y, '░', Theme.DIM, Theme.PANEL_BG)
        }
    }

    override fun close() = screen.stopScreen()
}
