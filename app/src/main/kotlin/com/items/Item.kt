package com.items

import com.player.Player

class Item( 
    val name: String,
    val description: String,
    val cost: Double,
    val effect: (Player) -> Unit 
){  
    fun use(player: Player){
        this.effect(player)
    }
}