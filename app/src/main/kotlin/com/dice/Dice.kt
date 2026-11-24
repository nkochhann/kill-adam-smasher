package com.dice

import kotlin.random.Random

class Dice(
    var max: Int,
    var minimun: Int
)
{
    fun roll(times: Int): Int
    {
        var result = 0
        
        for(i in 0..times){
            result += (minimun..max).random()
        }
        
        return result
    }
}