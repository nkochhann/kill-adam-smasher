package com.player

import com.game.Combatant
import com.weapons.DamageRoll
import com.weapons.Weapon
import com.weapons.Weapons

class Player(
    override var name: String,
    override var maxHealth: Int,
    var eddies: Double = 0.0,
    var weapon: Weapon = Weapons.all.first()
) : Combatant {
    override var health: Int = maxHealth
    var attackBonus: Int = 0
        private set

    override val attackModifier: Int get() = weapon.handleability + attackBonus
    override var defenseModifier: Int = 0
        private set

    override fun rollDamage(isCritical: Boolean): DamageRoll = weapon.rollDamage(isCritical)

    fun addDefense(amount: Int){ defenseModifier += amount }

    fun addAttack(amount: Int) { attackBonus += amount }

    /** Restores health, never above [maxHealth]. */
    fun heal(amount: Int) { health = minOf(maxHealth, health + amount) }
    
    fun calculateEddiesIncome(tookDamage: Int, receivedDamage: Int)
    {
        this.eddies = tookDamage + (receivedDamage * 1.5)
    }
    
    fun reset() 
    {
        health = maxHealth
        attackBonus = 0
    }
}