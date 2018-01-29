package com.cout970.modeler.gui.components

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toNullable
import com.cout970.vector.api.IVector2
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

    override fun build(ctx: RBuilder): Component = panel root@ {
        marginX(ctx, 5f)
        posY = 105f
        height = 465f
        border(3f) { greyColor }
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
                        it.objects.first()
                    } else null
                }
                .getOr(ObjectRefNone)

        val cube = (props.access.model.getObject(cubeRef) as? IObjectCube).asNullable()

        val pos = { cube.map { it.pos }.getOr(Vector3.ORIGIN) }
        val rotation = { cube.map { it.rotation }.getOr(Vector3.ORIGIN) }
        val size = { cube.map { it.size }.getOr(Vector3.ORIGIN) }
        val tex = { cube.map { it.textureOffset }.getOr(Vector2.ORIGIN) }
        val scale = { cube.map { it.textureSize }.getOr(Vector2.ORIGIN) }
        val disp = props.dispatcher
        var p = 5f

        +panel {
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
            valueInput({ size().xf }, "cube.size.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ size().yf }, "cube.size.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ size().zf }, "cube.size.z", cubeRef, vec2Of(185f, 20f))
        }

        +panel {
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
            valueInput({ pos().xf }, "cube.pos.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ pos().yf }, "cube.pos.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ pos().zf }, "cube.pos.z", cubeRef, vec2Of(185f, 20f))
        }

        +panel {
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
            valueInput({ rotation().xf }, "cube.rot.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ rotation().yf }, "cube.rot.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ rotation().zf }, "cube.rot.z", cubeRef, vec2Of(185f, 20f))
        }

        +panel {
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
            valueInput({ tex().xf }, "cube.tex.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ tex().yf }, "cube.tex.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ scale().xf }, "cube.tex.scale", cubeRef, vec2Of(185f, 20f))
        }
    }

    fun Panel.valueInput(getter: () -> Float, cmd: String, cubeRef: IObjectRef, pos: IVector2) {
        val properties = mapOf("cube_ref" to cubeRef, "command" to cmd)
        +ValueInput {
            ValueInput.Props(
                    dispatcher = props.dispatcher,
                    value = getter,
                    cmd = "update.template.cube",
                    metadata = properties,
                    enabled = cubeRef != ObjectRefNone,
                    pos = pos
            )
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