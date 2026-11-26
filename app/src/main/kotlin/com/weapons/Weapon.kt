package com.weapons

import com.dice.Dice
import com.error.GameError
import kotlin.random.Random

/** Damage of one landed attack. */
data class DamageRoll(val amount: Int, val critical: Boolean)

abstract class Weapon (
    val name: String,
    var damageDiceSides: Int,
    var numberOfDamageDices: Int,
    var criticalMultiplier: Int,
    var handleability: Int
){
    init {
        if(this.handleability !in 0..5){
            throw GameError("Invalid weapon handleability value.")
        }

        if(this.damageDiceSides !in 6..20){
            throw GameError("Invalid dice sides number.")
        }
    }

    /** Dice notation, e.g. "2d10". */
    val damageNotation: String
        get() = "${numberOfDamageDices}d${damageDiceSides}"

    /** Rolls the damage dice; a critical hit multiplies the result. */
    fun rollDamage(isCritical: Boolean): DamageRoll {
        val base = Dice(damageDiceSides).roll(numberOfDamageDices)
        return DamageRoll(if (isCritical) base * criticalMultiplier else base, isCritical)
    }
}