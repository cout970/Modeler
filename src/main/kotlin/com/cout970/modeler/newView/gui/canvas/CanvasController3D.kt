package com.cout970.modeler.newView.gui.canvas

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIVector

/**
 * Created by cout970 on 2017/05/02.
 */
class CanvasController3D : CanvasController() {

    override val canvas: Canvas = Canvas(this)
    override val rendererProvider: IRendererProvider = TODO("Implement this")

    fun getMatrixMVP(): IMatrix4 {
        return MatrixUtils.createOrthoMatrix(canvas.size.toIVector()) * state.cameraHandler.camera.matrixForUV
    }
}