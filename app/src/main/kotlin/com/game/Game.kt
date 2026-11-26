package com.game

import com.enemy.AdamSmasher
import com.player.Player
import com.weapons.Weapon

/**
 * Game logic: owns the player, Adam, the combat log and the turn state machine.
 * Combat alternates PLAYER_TURN -> ADAM_TURN -> PLAYER_TURN until someone dies.
 * Has no rendering code; call [tick] once per frame.
 */
class Game {
    val player = Player("V", 100, eddies = 250.0)
    val adam = AdamSmasher()
    val log = mutableListOf<String>()

    var state = GameState.MENU
    var lastExchange: Exchange? = null
        private set

    private var adamTurnTicks = 0
    private var endTicks = 0

    val isOver: Boolean get() = player.isDead() || adam.isDead()

    fun startCombat(weapon: Weapon) {
        player.weapon = weapon
        player.reset()
        adam.reset()
        log.clear()
        lastExchange = null
        endTicks = 0
        log += "${player.name} faces ${adam.name} with ${weapon.name}."
        state = GameState.PLAYER_TURN
    }

    /** Player's attack. If Adam survives, the turn passes to him. */
    fun playerAttack() {
        if (state != GameState.PLAYER_TURN || isOver) return
        record(Combat.resolve(player, adam))
        if (startEnding()) return
        log += "${adam.name}'s turn."
        adamTurnTicks = ADAM_DELAY_TICKS
        state = GameState.ADAM_TURN
    }

    fun openShop() {
        if (state == GameState.PLAYER_TURN && !isOver) state = GameState.SHOP
    }

    fun openWeaponSelect() {
        state = GameState.WEAPON_SELECT
    }

    /** Starts a new fight with the weapon already chosen. */
    fun restart() = startCombat(player.weapon)

    /** Esc: leaves the shop back to combat, anything else (including end screens) back to the menu. */
    fun back() {
        endTicks = 0
        state = if (state == GameState.SHOP) GameState.PLAYER_TURN else GameState.MENU
    }

    /** Per-frame update: animations and Adam's automatic turn. */
    fun tick() {
        adam.update()
        if (state == GameState.ADAM_TURN && --adamTurnTicks <= 0) {
            if (!isOver) record(Combat.resolve(adam, player))
            state = GameState.PLAYER_TURN
            startEnding()
        }
        if (endTicks > 0 && --endTicks == 0) {
            state = if (adam.isDead()) GameState.WIN else GameState.GAME_OVER
        }
    }

    /** If someone died, schedules the WIN / GAME_OVER state so the last hit stays visible briefly. */
    private fun startEnding(): Boolean {
        if (!isOver) return false
        endTicks = END_DELAY_TICKS
        return true
    }

    private fun record(exchange: Exchange) {
        lastExchange = exchange
        log += exchange.describe()
        when {
            adam.isDead() -> log += "${adam.name} is down."
            player.isDead() -> log += "${player.name} is flatlined."
        }
    }

    private companion object {
        /** ~1 second at 50 ms per frame, so the player sees their own result first. */
        const val ADAM_DELAY_TICKS = 20

        /** ~1.5 seconds before switching to the win / game over screen. */
        const val END_DELAY_TICKS = 30
    }
}
