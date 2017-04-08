package com.cout970.modeler.newView.gui

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.newView.gui.comp.CPanel
import com.cout970.modeler.newView.viewtarget.ModelViewTarget
import com.cout970.modeler.newView.viewtarget.ViewTarget
import com.cout970.modeler.view.scene.CameraHandler

/**
 * Created by cout970 on 2017/04/08.
 */
class Scene : CPanel() {

    val cameraHandler = CameraHandler()
    var viewTarget: ViewTarget = ModelViewTarget()


    var perpective: Boolean = true
        private set

    fun getMatrixMVP(): IMatrix4 {
        return Matrix4.IDENTITY
    }
}