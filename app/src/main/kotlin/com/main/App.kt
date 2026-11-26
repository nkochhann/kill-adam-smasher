package com.main

import com.game.Game
import com.game.GameState
import com.items.Items
import com.ui.screens.CombatScreen
import com.ui.screens.CombatView
import com.ui.screens.DiceRollView
import com.ui.screens.EndScreen
import com.ui.screens.EndView
import com.ui.screens.MainMenuScreen
import com.ui.screens.Screen
import com.ui.screens.ShopItemView
import com.ui.screens.ShopScreen
import com.ui.screens.UiEvent
import com.ui.screens.WeaponSelectScreen
import com.ui.screens.WeaponView
import com.ui.tui.Tui
import com.weapons.Weapons

/** Wires the game logic to the screens and runs the frame loop. */
fun runApp() {
    val game = Game()
    val menu = MainMenuScreen()
    val weaponSelect = WeaponSelectScreen(
        Weapons.all.map { WeaponView(it.name, it.damageNotation, it.criticalMultiplier, it.handleability) }
    )
    val combat = CombatScreen(game.toCombatView())
    // Placeholder items; purchasing is not implemented yet.
    val shop = ShopScreen(
        Items.all.map { ShopItemView(it.name, it.description, it.cost) },
        game.player.eddies
    )

    val victory = EndScreen(victory = true)
    val defeat = EndScreen(victory = false)

    var tick = 0L
    Tui().use { tui ->
        tui.start()
        while (true) {
            game.tick()
            combat.view = game.toCombatView()
            shop.eddies = game.player.eddies
            victory.view = game.toEndView()
            defeat.view = game.toEndView()

            val screen: Screen = when (game.state) {
                GameState.MENU -> menu
                GameState.WEAPON_SELECT -> weaponSelect
                GameState.PLAYER_TURN, GameState.ADAM_TURN -> combat
                GameState.SHOP -> shop
                GameState.WIN -> victory
                GameState.GAME_OVER -> defeat
            }
            tui.clear()
            screen.render(tui, tick)
            tui.refresh()

            val key = tui.readKey()
            if (key != null) {
                when (val event = screen.handleKey(key)) {
                    UiEvent.Start -> game.openWeaponSelect()
                    is UiEvent.SelectWeapon -> game.startCombat(Weapons.all[event.index])
                    UiEvent.Fight -> game.playerAttack()
                    UiEvent.OpenShop -> game.openShop()
                    UiEvent.Retry -> game.restart()
                    UiEvent.Back -> game.back()
                    UiEvent.Quit -> return
                    else -> {}
                }
            }
            tick++
            Thread.sleep(50)
        }
    }
}

/** Maps game state to what the win / game over screens draw. */
private fun Game.toEndView() = EndView(player.name, player.health, player.maxHealth, player.eddies)

/** Maps game state to what the combat screen draws. */
private fun Game.toCombatView(): CombatView {
    val ex = lastExchange
    return CombatView(
        player.name, player.health, player.maxHealth, player.eddies,
        adam.name, adam.health, adam.maxHealth, adam.state,
        ex?.let { DiceRollView(it.attacker.name, it.attack.d20, it.attack.modifier) },
        ex?.let { DiceRollView(it.defender.name, it.defense.d20, it.defense.modifier) },
        log
    )
}
