package com.player

import com.game.Combatant
import com.items.Item
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
    var damageBonus: Int = 0
        private set
    var criticalBonus: Int = 0
        private set

    override val attackModifier: Int get() = weapon.handleability
    override var defenseModifier: Int = 0
        private set

    override fun rollDamage(isCritical: Boolean): DamageRoll =
        weapon.rollDamage(isCritical, criticalBonus, damageBonus)

    fun addDefense(amount: Int){ defenseModifier += amount }

    fun addDamage(amount: Int) { damageBonus += amount }

    fun addCriticalBonus(amount: Int) { criticalBonus += amount }

    /** Restores health, never above [maxHealth]. */
    fun heal(amount: Int) { health = minOf(maxHealth, health + amount) }

    /** Pays for and applies [item]. Returns false (and changes nothing) if there are not enough eddies. */
    fun buy(item: Item): Boolean {
        if (eddies < item.cost) return false
        eddies -= item.cost
        item.use(this)
        return true
    }
    
    /** Adds the cycle's income to the eddies and returns the amount earned. */
    fun calculateEddiesIncome(tookDamage: Int, receivedDamage: Int): Double
    {
        val income = tookDamage + (receivedDamage * 1.5)
        this.eddies += income
        return income
    }

    /** Clears every item bonus (damage, defense and critical). */
    fun resetBonuses() {
        damageBonus = 0
        defenseModifier = 0
        criticalBonus = 0
    }
    
    fun reset() 
    {
        health = maxHealth
        resetBonuses()
    }
}