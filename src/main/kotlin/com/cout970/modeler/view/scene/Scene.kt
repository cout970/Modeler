package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.util.Cursor
import com.cout970.modeler.util.CursorParameters
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.selection.SelectorCache
import com.cout970.modeler.view.gui.comp.CBorderRenderer
import com.cout970.modeler.window.WindowHandler
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2016/12/27.
 */
abstract class Scene(val modelProvider: IModelProvider, val windowHandler: WindowHandler,
                     val sceneController: SceneController) : Panel() {

    abstract var perspective: Boolean

    val camera get() = cameraHandler.camera
    val cameraHandler = CameraHandler()

    val cursorParameters: CursorParameters get() = CursorParameters.create(camera.zoom, size.toIVector())
    val cursor: Cursor get() = sceneController.cursorTemplate.copy(parameters = cursorParameters)

    val selectorCache = SelectorCache()

    init {
        backgroundColor = ColorConstants.transparent()
        border.renderer = CBorderRenderer
    }

    open fun update() {
        cameraHandler.update(sceneController.timer)
    }

    abstract fun getMatrixMVP(): IMatrix4

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }
}

