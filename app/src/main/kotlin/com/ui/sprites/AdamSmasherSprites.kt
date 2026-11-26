package com.ui.sprites

enum class AdamState(val resource: String) {
    IDLE_1("/sprites/adam_idle_1.png"),
    IDLE_2("/sprites/adam_idle_2.png"),
    HIT("/sprites/adam_hit.png"),
    DEAD("/sprites/adam_dead.png")
}

/**
 * Adam Smasher sprites. A PNG in resources/sprites overrides the built-in
 * procedural pixel art, so you can drop in your own art at any time.
 */
object AdamSmasherSprites {
    private const val SIZE = 32

    private val cache = HashMap<AdamState, Sprite>()
    private val scaledCache = HashMap<Pair<AdamState, Double>, Sprite>()

    /** Sprite resized by [scale] (cached). */
    fun get(state: AdamState, scale: Double): Sprite =
        scaledCache.getOrPut(state to scale) { get(state).scaled(scale) }

    fun get(state: AdamState): Sprite = cache.getOrPut(state) {
        SpriteLoader.fromResource(state.resource) ?: generate(state)
    }

    // ---- procedural art ----
    private fun argb(r: Int, g: Int, b: Int) = (0xFF shl 24) or (r shl 16) or (g shl 8) or b

    private val CHROME = argb(170, 180, 195)
    private val STEEL = argb(95, 105, 125)
    private val DARK = argb(40, 44, 58)
    private val EYE = argb(255, 30, 40)
    private val EYE_DIM = argb(90, 20, 25)
    private val GLOW = argb(255, 160, 60)
    private val WHITE = argb(255, 255, 255)

    private class Canvas {
        val px = IntArray(SIZE * SIZE)
        fun rect(x0: Int, y0: Int, x1: Int, y1: Int, c: Int) {
            for (y in y0..y1) for (x in x0..x1) if (x in 0 until SIZE && y in 0 until SIZE) px[y * SIZE + x] = c
        }
        fun tint(c: Int) {
            for (i in px.indices) if (px[i] != 0) px[i] = c
        }
    }

    private fun generate(state: AdamState): Sprite = when (state) {
        AdamState.IDLE_1 -> standing(bob = 0, eye = EYE)
        AdamState.IDLE_2 -> standing(bob = 1, eye = GLOW)
        AdamState.HIT -> standing(bob = 1, eye = WHITE, dx = 2, flash = true)
        AdamState.DEAD -> dead()
    }

    private fun standing(bob: Int, eye: Int, dx: Int = 0, flash: Boolean = false): Sprite {
        val c = Canvas()
        val o = dx
        // legs (static)
        c.rect(10 + o, 22, 14 + o, 30, STEEL); c.rect(18 + o, 22, 22 + o, 30, STEEL)
        c.rect(9 + o, 29, 14 + o, 31, DARK); c.rect(18 + o, 29, 23 + o, 31, DARK)
        // pelvis
        c.rect(11 + o, 20 + bob, 21 + o, 22, DARK)
        // torso
        c.rect(9 + o, 11 + bob, 22 + o, 20 + bob, STEEL)
        c.rect(13 + o, 13 + bob, 18 + o, 17 + bob, DARK)
        c.rect(15 + o, 14 + bob, 16 + o, 16 + bob, GLOW) // reactor core
        // shoulders
        c.rect(4 + o, 9 + bob, 10 + o, 13 + bob, CHROME); c.rect(21 + o, 9 + bob, 27 + o, 13 + bob, CHROME)
        // arms (left fist, right heavy cannon)
        c.rect(4 + o, 14 + bob, 7 + o, 22 + bob, STEEL); c.rect(3 + o, 22 + bob, 8 + o, 24 + bob, DARK)
        c.rect(24 + o, 14 + bob, 28 + o, 20 + bob, STEEL); c.rect(24 + o, 21 + bob, 30 + o, 25 + bob, DARK)
        c.rect(29 + o, 22 + bob, 31, 23 + bob, GLOW)
        // head: chrome skull
        c.rect(12 + o, 2 + bob, 19 + o, 9 + bob, CHROME)
        c.rect(13 + o, 10 + bob, 18 + o, 10 + bob, STEEL) // neck
        c.rect(13 + o, 7 + bob, 18 + o, 9 + bob, STEEL)   // jaw
        c.rect(13 + o, 4 + bob, 14 + o, 6 + bob, DARK)    // left socket
        c.rect(17 + o, 4 + bob, 18 + o, 6 + bob, eye)     // cyber eye
        if (flash) {
            c.tint(argb(255, 120, 130)) // red-white flash silhouette
            c.rect(17 + o, 4 + bob, 18 + o, 6 + bob, WHITE) // keep the eye visible
        }
        return Sprite(SIZE, SIZE, c.px)
    }

    private fun dead(): Sprite {
        val c = Canvas()
        // collapsed body on the ground
        c.rect(4, 26, 27, 30, STEEL)
        c.rect(6, 23, 16, 26, CHROME)       // shoulder plate
        c.rect(18, 24, 26, 26, DARK)
        c.rect(0, 28, 6, 31, DARK)          // fallen fist
        c.rect(24, 30, 31, 31, DARK)        // dropped cannon
        // head rolled aside
        c.rect(19, 20, 25, 25, CHROME)
        c.rect(22, 21, 23, 23, EYE_DIM)     // dead eye
        // sparks
        c.rect(12, 19, 12, 19, GLOW); c.rect(15, 21, 15, 21, GLOW); c.rect(9, 21, 9, 21, GLOW)
        return Sprite(SIZE, SIZE, c.px)
    }
}
