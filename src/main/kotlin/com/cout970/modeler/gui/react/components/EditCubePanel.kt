package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.gui.IModelAccessor
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.marginX
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toNullable
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/27.
 */
class EditCubePanel : RComponent<EditCubePanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel root@ {
        marginX(ctx, 5f)
        posY = 132f
        height = 460f
        setBorderless()
        setTransparent()

        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state)
        }
        listenerMap.addListener(EventSelectionUpdate::class.java) {
            replaceState(state)
        }

        val cubeRef = props.access.modelSelection
                .toNullable()
                .flatMap {
                    if (isSelectingOneCube(props.access.model, it)) {
                        props.access.model.getSelectedObjectRefs(it).first()
                    } else null
                }
                .getOr(ObjectRef(-1))

        val cube = (props.access.model.getObject(cubeRef) as? IObjectCube).asNullable()

        val pos = { cube.map { it.pos }.getOr(Vector3.ORIGIN) }
        val rotation = { cube.map { it.rotation }.getOr(Vector3.ORIGIN) }
        val size = { cube.map { it.size }.getOr(Vector3.ORIGIN) }
        val tex = { cube.map { it.textureOffset }.getOr(Vector2.ORIGIN) }
        val scale = { cube.map { it.textureSize }.getOr(Vector2.ORIGIN) }
        val disp = props.dispatcher
        var p = 5f

        panel {
            // size
            width = this@root.width
            height = 110f
            posY = p
            p += 115f
            setBorderless()
            setTransparent()

            +FixedLabel("Size", 0f, 0f, width, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +ValueInput { ValueInput.Props(disp, { size().xf }, "cube.size.x", cubeRef, vec2Of(10f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { size().yf }, "cube.size.y", cubeRef, vec2Of(98f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { size().zf }, "cube.size.z", cubeRef, vec2Of(185f, 20f)) }
        }

        panel {
            // pos
            width = this@root.width
            height = 110f
            posY = p
            p += 115f
            setBorderless()
            setTransparent()

            +FixedLabel("Position", 0f, 0f, width, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +ValueInput { ValueInput.Props(disp, { pos().xf }, "cube.pos.x", cubeRef, vec2Of(10f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { pos().yf }, "cube.pos.y", cubeRef, vec2Of(98f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { pos().zf }, "cube.pos.z", cubeRef, vec2Of(185f, 20f)) }
        }

        panel {
            // rotation
            width = this@root.width
            height = 110f
            posY = p
            p += 115f
            setBorderless()
            setTransparent()

            +FixedLabel("Rotation", 0f, 0f, width, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +ValueInput { ValueInput.Props(disp, { rotation().xf }, "cube.rot.x", cubeRef, vec2Of(10f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { rotation().yf }, "cube.rot.y", cubeRef, vec2Of(98f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { rotation().zf }, "cube.rot.z", cubeRef, vec2Of(185f, 20f)) }
        }

        panel {
            // texture
            width = this@root.width
            height = 110f
            posY = p
            p += 115f
            setBorderless()
            setTransparent()

            +FixedLabel("Texture", 0f, 0f, width, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("scale", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +ValueInput { ValueInput.Props(disp, { tex().xf }, "cube.tex.x", cubeRef, vec2Of(10f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { tex().yf }, "cube.tex.y", cubeRef, vec2Of(98f, 20f)) }
            +ValueInput { ValueInput.Props(disp, { scale().xf }, "cube.tex.scale", cubeRef, vec2Of(185f, 20f)) }
        }
    }

    fun isSelectingOneCube(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    data class Props(val access: IModelAccessor, val dispatcher: Dispatcher)

    companion object : RComponentSpec<EditCubePanel, Props, Unit>
}