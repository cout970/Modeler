package com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.util.*
import com.cout970.modeler.view.gui.ComponentPresenter
import com.cout970.modeler.view.gui.comp.CTextInput
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond
import org.joml.Vector3d
import org.liquidengine.legui.component.TextInput
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2017/07/16.
 */

class EditCubePanelPresenter(val editCubePanel: EditCubePanel) : ComponentPresenter() {

    val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    val model get() = gui.projectManager.model
    val scriptEngine get() = CTextInput.scriptEngine
    var monitoredCube: IObjectRef? = null

    fun showCube(cube: IObjectRef) {
        monitoredCube = cube
        setSize()
        setPos()
        setRotation()
        editCubePanel.show()
    }

    fun onTextInput(input: CTextInput, offset: Float = 0f) {
        when (input.id) {
            "cube.size.x" -> setSize(x = getValue(input, getSize().xf) + offset)
            "cube.size.y" -> setSize(y = getValue(input, getSize().yf) + offset)
            "cube.size.z" -> setSize(z = getValue(input, getSize().zf) + offset)

            "cube.pos.x" -> setPos(x = getValue(input, getPos().xf) + offset)
            "cube.pos.y" -> setPos(y = getValue(input, getPos().yf) + offset)
            "cube.pos.z" -> setPos(z = getValue(input, getPos().zf) + offset)

            "cube.rot.x" -> setRotation(x = getValue(input, getRotation().xf) + offset)
            "cube.rot.y" -> setRotation(y = getValue(input, getRotation().yf) + offset)
            "cube.rot.z" -> setRotation(z = getValue(input, getRotation().zf) + offset)
        }
    }

    fun setSize(x: Float = getSize().xf, y: Float = getSize().yf, z: Float = getSize().zf) {

        val ref = monitoredCube ?: return
        val oldObj = model.objects[ref.objectIndex] as ObjectCube
        val newSize = vec3Of(Math.max(0f, x), Math.max(0f, y), Math.max(0f, z))
        val size: IVector3

        if (newSize != oldObj.size) {
            val newObj = oldObj.copy(size = newSize)
            size = newSize
            gui.actionExecutor.actionTrigger.changeObject(ref, newObj)
        } else {
            size = getSize()
        }
        val panel = editCubePanel.sizePanel
        panel.sizeXInput.text = formatter.format(size.xf)
        panel.sizeYInput.text = formatter.format(size.yf)
        panel.sizeZInput.text = formatter.format(size.zf)
    }

    fun getSize(): IVector3 {
        val ref = monitoredCube ?: return Vector3.ORIGIN
        return (model.objects[ref.objectIndex] as ObjectCube).size
    }

    fun setPos(x: Float = getPos().xf, y: Float = getPos().yf, z: Float = getPos().zf) {

        val ref = monitoredCube ?: return
        val oldObj = model.objects[ref.objectIndex] as ObjectCube
        val newPos = vec3Of(x, y, z)
        val pos: IVector3

        if (newPos != oldObj.pos) {
            val newObj = oldObj.copy(pos = newPos)
            pos = newPos
            gui.actionExecutor.actionTrigger.changeObject(ref, newObj)
        } else {
            pos = getPos()
        }
        val panel = editCubePanel.posPanel
        panel.posXInput.text = formatter.format(pos.xf)
        panel.posYInput.text = formatter.format(pos.yf)
        panel.posZInput.text = formatter.format(pos.zf)
    }

    fun getPos(): IVector3 {
        val ref = monitoredCube ?: return Vector3.ORIGIN
        return (model.objects[ref.objectIndex] as ObjectCube).pos
    }

    fun setRotation(x: Float = getRotation().xf, y: Float = getRotation().yf, z: Float = getRotation().zf) {

        val ref = monitoredCube ?: return
        val oldObj = model.objects[ref.objectIndex] as ObjectCube
        val newRot = Quaterniond().rotationXYZ(
                Math.toRadians(x.toDouble()),
                Math.toRadians(y.toDouble()),
                Math.toRadians(z.toDouble())
        ).toIQuaternion()

        val rot: IVector3

        if (newRot != oldObj.rotation) {
            val newObj = oldObj.copy(rotation = newRot)
            rot = newRot.toJOML().getEulerAnglesXYZ(Vector3d()).toIVector().toDegrees()
            gui.actionExecutor.actionTrigger.changeObject(ref, newObj)
        } else {
            rot = getRotation()
        }
        val panel = editCubePanel.rotationPanel
        panel.rotXInput.text = formatter.format(rot.xf)
        panel.rotYInput.text = formatter.format(rot.yf)
        panel.rotZInput.text = formatter.format(rot.zf)
    }

    fun getRotation(): IVector3 {
        val ref = monitoredCube ?: return Vector3.ORIGIN
        val rot = (model.objects[ref.objectIndex] as ObjectCube).rotation
        val angles = rot.toJOML().getEulerAnglesXYZ(Vector3d())
        return angles.toIVector().toDegrees()
    }

    fun getValue(input: TextInput, default: Float): Float {
        try {
            return (scriptEngine.eval(input.text) as? Number)?.toFloat() ?: default
        } catch (e: Exception) {
            return default
        }
    }
}