package com.cout970.modeler.view.gui.editor

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.util.*
import com.cout970.modeler.view.gui.ComponentUpdater
import com.cout970.modeler.view.gui.comp.CTextInput
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond
import org.joml.Vector3d
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.context.LeguiContext
import org.liquidengine.legui.event.component.FocusEvent
import org.liquidengine.legui.event.component.KeyboardKeyEvent
import org.liquidengine.legui.listener.LeguiEventListenerMap
import org.lwjgl.glfw.GLFW
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2017/07/08.
 */

class LeftPanelUpdater : ComponentUpdater() {

    val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    val model get() = gui.actionExecutor.model
    val leguiContext: LeguiContext get() = gui.guiUpdater.leguiContext
    val scriptEngine get() = gui.guiUpdater.scriptEngine
    var monitoredCube: IObjectRef? = null

    override fun onModelUpdate(old: IModel, new: IModel) {
        onSelectionUpdate(null, gui.selectionHandler.getSelection())
    }

    override fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        val panel = gui.editorPanel.leftPanel.editCubePanel
        val model = gui.actionExecutor.model

        if (new != null && isSelectingOneCube(new)) {
            showCube(model.getSelectedObjectRefs(new).first())
        } else {
            if (monitoredCube != null && leguiContext.focusedGui is CTextInput) {
                updateTextInput(leguiContext.focusedGui as CTextInput)
            }
            monitoredCube = null
            panel.hide()
        }
    }

    fun isSelectingOneCube(new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    fun showCube(cube: IObjectRef) {
        monitoredCube = cube
        setSize()
        setPos()
        setRotation()
        gui.editorPanel.leftPanel.editCubePanel.show()
    }

    fun updateTextInput(input: CTextInput, offset: Float = 0f) {
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

    fun getValue(input: TextInput, default: Float): Float {
        try {
            return (scriptEngine.eval(input.text) as? Number)?.toFloat() ?: default
        } catch (e: Exception) {
            return default
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
        val panel = gui.editorPanel.leftPanel.editCubePanel.sizePanel
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
            println("old: ${oldObj}")
            println("new: ${newObj}")
            gui.actionExecutor.actionTrigger.changeObject(ref, newObj)
        } else {
            pos = getPos()
        }
        val panel = gui.editorPanel.leftPanel.editCubePanel.posPanel
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
        val panel = gui.editorPanel.leftPanel.editCubePanel.rotationPanel
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

    fun handleKeyPress(input: CTextInput, event: KeyboardKeyEvent) {
        if (event.key == Keyboard.KEY_ENTER) {
            updateTextInput(input)
        }
    }

    fun handleFocusChange(input: CTextInput, event: FocusEvent) {
        if (event.focusGained) {
            if (input.text.isNotEmpty()) {
                input.startSelectionIndex = 0
                input.endSelectionIndex = input.text.length
            }
        } else {
            updateTextInput(input)
        }
    }

    override fun handleScroll(e: EventMouseScroll): Boolean {
        val target = leguiContext.mouseTargetGui
        if (target is CTextInput) {
            updateTextInput(target, e.offsetY.toFloat())
        }
        return false
    }

    override fun bindTextInputs(panel: Panel) {
        panel.components.forEach {
            if (it is CTextInput) {
                it.leguiEventListeners.setKeyboardListener { e -> handleKeyPress(it, e) }
                it.leguiEventListeners.setFocusListener { e -> handleFocusChange(it, e) }
            } else if (it is Panel) {
                bindTextInputs(it)
            }
        }
    }

    fun LeguiEventListenerMap.setKeyboardListener(function: (KeyboardKeyEvent) -> Unit) {
        getListeners(KeyboardKeyEvent::class.java).toList().forEach {
            removeListener(KeyboardKeyEvent::class.java, it)
        }
        addListener(KeyboardKeyEvent::class.java, { if (it.action == GLFW.GLFW_PRESS) function(it) })
    }

    fun LeguiEventListenerMap.setFocusListener(function: (FocusEvent) -> Unit) {
        getListeners(FocusEvent::class.java).toList().forEach {
            removeListener(FocusEvent::class.java, it)
        }
        addListener(FocusEvent::class.java, { function(it) })
    }
}