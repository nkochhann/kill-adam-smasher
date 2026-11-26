package com.ui.sprites

import javax.imageio.ImageIO

object SpriteLoader {
    /** Loads a PNG from the classpath (e.g. "/sprites/foo.png"). Returns null if missing. */
    fun fromResource(path: String): Sprite? {
        val stream = SpriteLoader::class.java.getResourceAsStream(path) ?: return null
        val img = stream.use { ImageIO.read(it) } ?: return null
        val px = IntArray(img.width * img.height)
        img.getRGB(0, 0, img.width, img.height, px, 0, img.width)
        return Sprite(img.width, img.height, px)
    }
}
