package com.cout970.modeler.view.controller

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IInput
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.newView.gui.Root
import com.cout970.modeler.util.BooleanProperty
import com.cout970.modeler.util.Cache
import com.cout970.modeler.util.Cursor
import com.cout970.modeler.util.CursorParameters
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/12/27.
 */

class SceneController(val modelProvider: IModelProvider, val input: IInput, val rootFrame: Root,
                      val timer: Timer) {

    lateinit var selectedScene: Scene
    val scenes = mutableListOf<Scene>()

    val cursorCenter: IVector3 get() {
        return modelProvider.selectionManager.getSelectionCenter(
                selectedScene.selectorCache.model ?: modelProvider.model)
    }

    val modelCache = Cache<Int, VAO>(20).apply { onRemove = { _, v -> v.close() } }
    val selectionCache = Cache<Int, VAO>(40).apply { onRemove = { _, v -> v.close() } }

    var transformationMode = TransformationMode.TRANSLATION

    val showAllMeshUVs = BooleanProperty(true)
    val showBoundingBoxes = BooleanProperty(false)

    val cursorTemplate: Cursor get() = Cursor(cursorCenter, transformationMode, CursorParameters.create(0.0, vec2Of(1)))

    val listeners = ViewListeners(modelProvider, input, this)

    fun registerListeners(eventHandler: IEventController) {
        listeners.registerListeners(eventHandler)
    }

    fun tick() {
        scenes.forEach(Scene::update)
    }

    fun getModel(model: Model): Model {
        if (selectedScene.selectorCache.model != null) {
            return selectedScene.selectorCache.model!!
        }
        return model
    }

}