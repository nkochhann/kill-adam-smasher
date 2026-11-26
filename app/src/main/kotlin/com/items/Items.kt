package com.items

/** Catalog of the items sold in the shop. */
object Items {
    val all: List<Item> = listOf(
        Item("MaxDoc", "Heals 30 HP", 50.0) { it.heal(30) },
        Item("Stimulant", "+3 attack until the fight ends", 80.0) { it.addAttack(3) },
        Item("Subdermal Armor", "+2 defense", 120.0) { it.addDefense(2) },
        Item("Critical Chip", "+1 critical multiplier", 400.0) { it.weapon.criticalMultiplier += 1 }
    )
}
