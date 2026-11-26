package com.game

import com.weapons.DamageRoll

/** One D20 roll plus the modifier of whoever rolled it. */
data class Roll(val d20: Int, val modifier: Int) {
    val total: Int get() = d20 + modifier

    /** A natural 20 on the die is a critical hit. */
    val isCritical: Boolean get() = d20 == 20
}

/** Result of a single attack: both D20 rolls and, if it landed, the damage dealt. */
data class Exchange(
    val attacker: Combatant,
    val defender: Combatant,
    val attack: Roll,
    val defense: Roll,
    val damage: DamageRoll?
) {
    val hit: Boolean get() = damage != null

    /** One-line summary for the combat log. */
    fun describe(): String =
        if (damage == null) "${defender.name} blocks ${attacker.name}'s attack."
        else "${attacker.name} hits ${defender.name} for ${damage.amount}" +
            (if (damage.critical) " (CRITICAL!)." else ".")
}

object Combat {
    /**
     * Attacker rolls D20 + attack modifier, defender rolls D20 + defense modifier.
     * The attack lands only if its total is higher, or automatically on a natural 20 (critical hit).
     * Damage is applied to the defender.
     */
    fun resolve(attacker: Combatant, defender: Combatant): Exchange {
        val attack = attacker.rollAttack()
        val defense = defender.rollDefense()
        val lands = attack.isCritical || attack.total > defense.total
        val damage = if (lands) attacker.rollDamage(attack.isCritical) else null
        damage?.let { defender.takeDamage(it.amount) }
        return Exchange(attacker, defender, attack, defense, damage)
    }
}
