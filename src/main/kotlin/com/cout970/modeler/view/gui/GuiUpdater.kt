package com.cout970.modeler.view.gui

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.util.*
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.gui.comp.CTextInput
import com.cout970.vector.api.IVector2
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
import javax.script.ScriptEngineManager


/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater {

    lateinit var leguiContext: LeguiContext
    lateinit var gui: Gui
    val scriptEngine = ScriptEngineManager().getEngineByName("JavaScript")!!
    var monitoredCube: IObjectRef? = null
    val helper = CubeHelper()

    fun updateTextInput(input: CTextInput, offset: Float = 0f) {
        when (input.id) {
            "cube.size.x" -> helper.setSize(x = getValue(input, helper.getSize().xf) + offset)
            "cube.size.y" -> helper.setSize(y = getValue(input, helper.getSize().yf) + offset)
            "cube.size.z" -> helper.setSize(z = getValue(input, helper.getSize().zf) + offset)
            "cube.pos.x" -> helper.setPos(x = getValue(input, helper.getPos().xf) + offset)
            "cube.pos.y" -> helper.setPos(y = getValue(input, helper.getPos().yf) + offset)
            "cube.pos.z" -> helper.setPos(z = getValue(input, helper.getPos().zf) + offset)
            "cube.rot.x" -> helper.setRotation(x = getValue(input, helper.getRotation().xf) + offset)
            "cube.rot.y" -> helper.setRotation(y = getValue(input, helper.getRotation().yf) + offset)
            "cube.rot.z" -> helper.setRotation(z = getValue(input, helper.getRotation().zf) + offset)
        }
    }

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        updateSizes(vec2Of(event.width, event.height))
        return false
    }

    fun updateSizes(newSize: IVector2) {
        gui.root.apply {
            size = newSize.toJoml2f()
            mainPanel?.updateSizes(newSize)
        }
        gui.canvasContainer.layout.updateCanvas()
    }

    fun getValue(input: TextInput, default: Float): Float {
        try {
            return (scriptEngine.eval(input.text) as? Number)?.toFloat() ?: default
        } catch (e: Exception) {
            return default
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectionChange(old: ISelection?, new: ISelection?) {
        val panel = gui.editorPanel.leftPanel.editCubePanel
        val model = gui.modelTransformer.model

        if (new != null &&
            new.selectionType == SelectionType.OBJECT &&
            new.selectionTarget == SelectionTarget.MODEL &&
            new.size == 1 &&
            model.getSelectedObjects(new).firstOrNull()?.let { it is ObjectCube } ?: false) {

            monitoredCube = model.getSelectedObjectRefs(new).first()
            panel.enable()
            helper.setSize()
            helper.setPos()
            helper.setRotation()
        } else {
            if (monitoredCube != null && leguiContext.focusedGui is CTextInput) {
                updateTextInput(leguiContext.focusedGui as CTextInput)
            }
            monitoredCube = null
            panel.disable()
        }
        updateObjectList()
    }

    fun updateObjectList() {
        val tree = gui.editorPanel.rightPanel.treeViewPanel
        val model = gui.modelTransformer.model

        tree.clear()
        model.objects
                .mapIndexed { index, _ -> ObjectRef(index) }
                .forEach { tree.addItem(it, model, gui.resources) }

        val materials = gui.editorPanel.rightPanel.materialListPanel

        materials.clear()
        model.objects
                .map { it.material }
                .distinct()
                .forEach { materials.addItem(it, gui.resources) }
    }

    fun findTextInput(id: String, panel: Panel): CTextInput? {
        panel.components.forEach {
            if (it is CTextInput) {
                return it
            } else if (it is Panel) {
                return findTextInput(id, it)
            }
        }
        return null
    }

    fun bindTextInputs(panel: Panel) {
        panel.components.forEach {
            if (it is CTextInput) {
                it.leguiEventListeners.setKeyboardListener { e -> handleKeyPress(it, e) }
                it.leguiEventListeners.setFocusListener { e -> handleFocusChange(it, e) }
            } else if (it is Panel) {
                bindTextInputs(it)
            }
        }
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

    fun handleScroll(e: EventMouseScroll): Boolean {
        val target = leguiContext.mouseTargetGui
        if (target is CTextInput) {
            updateTextInput(target, e.offsetY.toFloat())
        }
        return false
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

    inner class CubeHelper {

        val formatter = DecimalFormat("#.###")

        val model get() = gui.modelTransformer.model

        fun setSize(x: Float = getSize().xf, y: Float = getSize().yf, z: Float = getSize().zf) {

            val ref = monitoredCube ?: return
            val oldObj = model.objects[ref.objectIndex] as ObjectCube
            val newSize = vec3Of(Math.max(0f, x), Math.max(0f, y), Math.max(0f, z))
            val size: IVector3

            if (newSize != oldObj.size) {
                val newObj = oldObj.copy(size = newSize)
                size = newSize
                gui.modelTransformer.changeObject(ref, newObj)
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
                gui.modelTransformer.changeObject(ref, newObj)
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
                gui.modelTransformer.changeObject(ref, newObj)
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
    }
}