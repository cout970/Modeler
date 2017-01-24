package com.cout970.modeler.view.gui

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.resource.ResourceLoader
import org.liquidengine.legui.component.ImageView

/**
 * Created by cout970 on 2017/01/24.
 */
class TextureHandler(loader: ResourceLoader) {

    val debugTexture: Texture

    init {
        debugTexture = loader.getTexture("assets/textures/debug.png")
        ImageView(loader.getImage("assets/textures/debug.png"))
    }
}