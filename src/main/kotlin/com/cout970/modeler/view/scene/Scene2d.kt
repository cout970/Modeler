package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/01/02.
 */
class Scene2d(modelEditor: ModelEditor, windowHandler: WindowHandler, controller: SceneController) : Scene(
        modelEditor, windowHandler, controller) {

    override var perspective = false

    init {
        cameraHandler.setRotation(angleX = 0.0, angleY = 0.0)
    }


    override fun getMatrixMVP(): IMatrix4 {
        return MatrixUtils.createOrthoMatrix(size.toIVector()) * camera.matrixForUV
    }

    fun fromTextureToWorld(point: IVector2): IVector3 {
        val texture = modelProvider.model.resources.materials.firstOrNull() ?: MaterialNone
        val size = texture.size
        val offset = size / 2

        val min = vec2Of(-offset.xi * (size.xi / size.xi), -offset.yi)
        val max = vec2Of(-offset.xi + size.xi * (size.xi / size.xi), size.yi - offset.yi)

        return vec3Of(min.xd + (max.xd - min.xd) * point.xd, min.yd + (max.yd - min.yd) * (1 - point.yd), 0)
    }
}