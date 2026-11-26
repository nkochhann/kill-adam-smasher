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

    override val attackModifier: Int get() = weapon.handleability + 2
    override val defenseModifier: Int get() = 3

    override fun rollDamage(isCritical: Boolean): DamageRoll = weapon.rollDamage(isCritical)

    fun calculateEddiesIncome(tookDamage: Int, receivedDamage: Int)
    {
        this.eddies = tookDamage + (receivedDamage * 1.5)
    }
    
    fun reset() 
    {
        health = maxHealth
    }
}