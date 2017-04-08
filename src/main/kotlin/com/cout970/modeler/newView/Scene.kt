package com.cout970.modeler.newView

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.view.scene.CameraHandler
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/04/08.
 */
class Scene : Panel() {

    val cameraHandler = CameraHandler()
    var viewTarget: ViewTarget = ModelViewTarget()


    var perpective: Boolean = true
        private set

    fun getMatrixMVP(): IMatrix4 {
        return Matrix4.IDENTITY
    }
}