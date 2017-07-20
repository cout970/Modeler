package com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel

import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.util.show
import com.cout970.modeler.util.text
import com.cout970.modeler.view.gui.ComponentPresenter
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.wf
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
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
        setRotation(cube.rotation)
        editCubePanel.show()
    }

    fun setSize(size: IVector3) {
        val panel = editCubePanel.sizePanel
        panel.sizeXInput.text = formatter.format(size.xf)
        panel.sizeYInput.text = formatter.format(size.yf)
        panel.sizeZInput.text = formatter.format(size.zf)
    }

    fun setPos(pos: IVector3) {
        val panel = editCubePanel.posPanel
        panel.posXInput.text = formatter.format(pos.xf)
        panel.posYInput.text = formatter.format(pos.yf)
        panel.posZInput.text = formatter.format(pos.zf)
    }

    fun setRotation(rot: IQuaternion) {
        val panel = editCubePanel.rotationPanel
        panel.rotXInput.text = formatter.format(rot.xf)
        panel.rotYInput.text = formatter.format(rot.yf)
        panel.rotZInput.text = formatter.format(rot.zf)
        panel.rotWInput.text = formatter.format(rot.wf)
    }
}
