package com.weapons

class IconicWeapon(
    name: String,
    damageDiceSides: Int,
    numberOfDamageDices: Int,
    criticalMultiplier: Int,
    handleability: Int
) : Weapon(name, damageDiceSides, numberOfDamageDices, criticalMultiplier, handleability)

/** Placeholder catalog; replace with the final weapons when they are designed. */
object Weapons {
    val all: List<Weapon> = listOf(
        IconicWeapon("Malorian Arms 3516", 10, 2, 3, 3),
        IconicWeapon("Skippy", 6, 2, 2, 5),
        IconicWeapon("Mantis Blades", 6, 3, 2, 4),
        IconicWeapon("Comrade's Hammer", 20, 1, 4, 1)
    )
}
