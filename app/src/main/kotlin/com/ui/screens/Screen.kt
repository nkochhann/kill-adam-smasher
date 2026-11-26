package com.ui.screens

import com.googlecode.lanterna.input.KeyStroke
import com.ui.tui.Tui

sealed interface UiEvent {
    data object None : UiEvent
    data object Start : UiEvent
    data object Quit : UiEvent
    data object Back : UiEvent
    data object Fight : UiEvent
    data object OpenShop : UiEvent
    data object Retry : UiEvent
    data class Buy(val index: Int) : UiEvent
    data class SelectWeapon(val index: Int) : UiEvent
}

interface Screen {
    fun render(tui: Tui, tick: Long)
    fun handleKey(key: KeyStroke): UiEvent
}
