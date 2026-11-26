package com.game

import com.dice.Dice
import com.weapons.DamageRoll

/** Anything that can fight: the player and Adam Smasher. */
interface Combatant {
    val name: String
    val maxHealth: Int
    var health: Int
    val attackModifier: Int
    val defenseModifier: Int

    /** Damage dealt when an attack lands. Each combatant rolls its own damage dice. */
    fun rollDamage(isCritical: Boolean): DamageRoll

    fun isDead(): Boolean = health <= 0

    fun takeDamage(amount: Int) {
        health = maxOf(0, health - amount)
    }

    fun rollAttack(): Roll = Roll(Dice(20).roll(1), attackModifier)

    fun rollDefense(): Roll = Roll(Dice(20).roll(1), defenseModifier)
}
