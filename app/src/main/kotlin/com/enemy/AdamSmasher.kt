package com.enemy

import com.dice.Dice
import com.game.Combatant
import com.ui.sprites.AdamState
import com.weapons.DamageRoll

class AdamSmasher(override var maxHealth: Int = 200) : Combatant {
    override val name: String = "ADAM SMASHER"
    override var health: Int = maxHealth
    var state: AdamState = AdamState.IDLE_1
    var damageDiceSides: Int = 10
    var numberOfDamageDices: Int = 2

    override val attackModifier: Int = 5
    override val defenseModifier: Int = 5

    private var hitTicksLeft = 0
    
    /** Adam rolls his own damage dice; his hits are never multiplied, even on a natural 20. */
    override fun rollDamage(isCritical: Boolean): DamageRoll =
        DamageRoll(Dice(damageDiceSides).roll(numberOfDamageDices), false)

    /** Takes damage and picks the matching sprite state (HIT briefly, DEAD at 0 HP). */
    override fun takeDamage(amount: Int) {
        super.takeDamage(amount)
        if (isDead()) {
            state = AdamState.DEAD
        } else {
            state = AdamState.HIT
            hitTicksLeft = HIT_TICKS
        }
    }

    /** Called once per frame: returns from the HIT sprite to idle. */
    fun update() {
        if (state == AdamState.HIT && --hitTicksLeft <= 0) state = AdamState.IDLE_1
    }

    fun reset() {
        health = maxHealth
        state = AdamState.IDLE_1
        hitTicksLeft = 0
    }

    private companion object {
        const val HIT_TICKS = 10
    }
}