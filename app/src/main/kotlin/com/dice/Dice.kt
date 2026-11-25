package com.dice

import kotlin.random.Random

class Dice(
    var sides: Int
)
{
    fun roll(times: Int): Int
    {
        var result = 0
        
        for(i in 0..times){
            result += (1..sides).random()
        }
        
        return result
    }
}