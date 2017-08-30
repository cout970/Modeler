package com.cout970.modeler.gui.editor.leftpanel.editcubepanel

import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.gui.ComponentPresenter
import com.cout970.modeler.util.show
import com.cout970.modeler.util.text
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.toDegrees
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2017/07/16.
 */

class EditCubePanelPresenter(val editCubePanel: EditCubePanel) : ComponentPresenter() {

    val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

    fun showCube(cube: IObjectCube) {
        setSize(cube.size)
        setPos(cube.pos)
        setRotation(cube.subTransformation.rotation.toDegrees())
        setRotationPos(cube.subTransformation.preRotation)
        setTextureOffset(cube.textureOffset)
        editCubePanel.show()
    }

    fun setSize(size: IVector3) {
        val panel = editCubePanel.sizePanel
        panel.sizeX.textField.text = formatter.format(size.xf)
        panel.sizeY.textField.text = formatter.format(size.yf)
        panel.sizeZ.textField.text = formatter.format(size.zf)
    }

    fun setPos(pos: IVector3) {
        val panel = editCubePanel.posPanel
        panel.posX.textField.text = formatter.format(pos.xf)
        panel.posY.textField.text = formatter.format(pos.yf)
        panel.posZ.textField.text = formatter.format(pos.zf)
    }
    fun setRotation(rot: IVector3) {
        val panel = editCubePanel.rotationPanel
        panel.rotX.textField.text = formatter.format(rot.xf)
        panel.rotY.textField.text = formatter.format(rot.yf)
        panel.rotZ.textField.text = formatter.format(rot.zf)
    }

    fun setRotationPos(pos: IVector3) {
        val panel = editCubePanel.rotationPosPanel
        panel.pivotX.textField.text = formatter.format(pos.xf)
        panel.pivotY.textField.text = formatter.format(pos.yf)
        panel.pivotZ.textField.text = formatter.format(pos.zf)
    }

    fun setTextureOffset(pos: IVector2) {
        val panel = editCubePanel.textureOffsetPanel
        panel.texX.textField.text = formatter.format(pos.xf)
        panel.texY.textField.text = formatter.format(pos.yf)
    }
}
