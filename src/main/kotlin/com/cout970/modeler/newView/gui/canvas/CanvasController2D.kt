package com.cout970.modeler.newView.gui.canvas

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIVector

/**
 * Created by cout970 on 2017/05/03.
 */
class CanvasController2D : CanvasController() {

    override val canvas: Canvas = Canvas(this)
    override val rendererProvider: IRendererProvider = TODO("Implement this")

    fun getMatrixMVP(): IMatrix4 {
        val projection = MatrixUtils.createOrthoMatrix(canvas.size.toIVector())
        val view = state.cameraHandler.camera.matrixForOrtho

        return projection * view
    }
}