package com.cout970.modeler.resource

import com.cout970.modeler.model.Model
import org.joml.Vector2f
import org.liquidengine.legui.component.ImageView

/**
 * Created by cout970 on 2017/01/24.
 */
class TextureHandler(val loader: ResourceLoader) {

    val selectionModeElement: ImageView
    val selectionModeEdge: ImageView
    val selectionModeQuad: ImageView
    val selectionModeVertex: ImageView
    val cursorTranslate: ImageView
    val cursorRotate: ImageView
    val cursorScale: ImageView

    init {
        selectionModeElement = ImageView(
                loader.getImage("assets/textures/selection_mode_element.png")).apply { size = Vector2f(32f) }

        selectionModeQuad = ImageView(
                loader.getImage("assets/textures/selection_mode_quad.png")).apply { size = Vector2f(32f) }

        selectionModeEdge = ImageView(
                loader.getImage("assets/textures/selection_mode_edge.png")).apply { size = Vector2f(32f) }

        selectionModeVertex = ImageView(
                loader.getImage("assets/textures/selection_mode_vertex.png")).apply { size = Vector2f(32f) }

        cursorTranslate = ImageView(loader.getImage("assets/textures/translation.png")).apply { size = Vector2f(32f) }
        cursorRotate = ImageView(loader.getImage("assets/textures/rotation.png")).apply { size = Vector2f(32f) }
        cursorScale = ImageView(loader.getImage("assets/textures/scale.png")).apply { size = Vector2f(32f) }
    }

    fun updateMaterials(model: Model) {
        //TODO use texturizer
//        model.groups.map { it.material }.filter { it.hasChanged() }.forEach { it.loadTexture(loader) }
    }
}