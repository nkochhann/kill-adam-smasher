package com.weapons

import com.error.GameError

abstract class Weapon (
    var baseDamage: Int,
    var criticalChance: Int,
    var criticalMultiplier: Int,
    var handleability: Int,
){
    init {
        if(this.handleability !in 0..5){
            throw GameError("Invalid weapon handleability value.")
        }
        
        if(this.criticalChance !in 0..100){
            throw GameError("Invalid weapon critical chance value.")
        }
    }
}