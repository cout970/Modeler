package com.cout970.modeler.view.render

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.to_redo.model.material.MaterialNone

/**
 * Created by cout970 on 2017/05/25.
 */
object Textures {

    lateinit var cursorTexture: Texture

    fun load(resourceLoader: ResourceLoader) {
        log(Level.FINE) { "[Textures] Loading cursor texture" }
        cursorTexture = resourceLoader.getTexture("assets/textures/cursor.png")
        log(Level.FINE) { "[Textures] Loading empty texture" }
        MaterialNone.loadTexture(resourceLoader)
    }
}