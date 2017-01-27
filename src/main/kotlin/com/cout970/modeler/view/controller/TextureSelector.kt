package com.cout970.modeler.view.controller

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.view.scene.SceneTexture

/**
 * Created by cout970 on 2017/01/27.
 */
class TextureSelector(val scene: SceneTexture, val controller: SceneController, val modelEditor: ModelEditor) {

    val transformationMode get() = controller.textureTransformationMode
}