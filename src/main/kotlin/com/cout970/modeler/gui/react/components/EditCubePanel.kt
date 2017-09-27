package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.gui.IModelAccessor
import com.cout970.modeler.gui.comp.VariableInput
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.hide
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/27.
 */
class EditCubePanel : RComponent<EditCubePanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        posY = 28f
        width = 280f
        height = 500f
        setBorderless()
        setTransparent()

        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state)
        }
        listenerMap.addListener(EventSelectionUpdate::class.java) {
            replaceState(state)
        }

        props.access.selection.getOrNull().let {
            if (it != null) {
                if (!isSelectingOneCube(props.access.model, it)) {
                    hide()
                }
            } else {
                hide()
            }
        }

        var p = 5f

        panel {
            // size
            width = 280f
            height = 90f
            posY = p
            p += 126f
            setBorderless()
            setTransparent()

            +FixedLabel("Size", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
            +VariableInput("cube.size.x", 14f, 20f)
            +VariableInput("cube.size.y", 103f, 20f)
            +VariableInput("cube.size.z", 192f, 20f)
        }

        panel {
            // pos
            width = 280f
            height = 90f
            posY = p
            p += 126f
            setBorderless()
            setTransparent()

            +FixedLabel("Position", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
            +VariableInput("cube.pos.x", 14f, 20f)
            +VariableInput("cube.pos.y", 103f, 20f)
            +VariableInput("cube.pos.z", 192f, 20f)
        }

        panel {
            // rotation
            width = 280f
            height = 90f
            posY = p
            p += 126f
            setBorderless()
            setTransparent()

            +FixedLabel("Rotation", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
            +VariableInput("cube.rot.x", 14f, 20f)
            +VariableInput("cube.rot.y", 103f, 20f)
            +VariableInput("cube.rot.z", 192f, 20f)
        }

        panel {
            // texture
            width = 280f
            height = 90f
            posY = p
            p += 126f
            setBorderless()
            setTransparent()

            +FixedLabel("Texture", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
            +VariableInput("cube.tex.x", 14f, 20f)
            +VariableInput("cube.tex.y", 103f, 20f)
            +VariableInput("cube.tex.scale", 192f, 20f)
        }
    }

    fun isSelectingOneCube(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    data class Props(val access: IModelAccessor)

    companion object : RComponentSpec<EditCubePanel, Props, Unit>
}