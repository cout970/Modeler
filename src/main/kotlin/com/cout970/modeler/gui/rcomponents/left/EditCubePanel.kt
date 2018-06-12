package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.alignAsColumn
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.rcomponents.FloatInput
import com.cout970.modeler.gui.rcomponents.FloatInputProps
import com.cout970.modeler.gui.rcomponents.TransformationInput
import com.cout970.modeler.gui.rcomponents.TransformationInputProps
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.DivBuilder
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.vec2Of

class EditCubePanel : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditCubePanel") {
        style {
            classes("left_panel_group", "edit_cube")
            height = if (state.on) 600f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 14f)
        }

        val (ref, cube) = getObject().split { it }
        val cubeRef: IObjectRef = ref.getOr(ObjectRefNone)

        val trans = cube.map { it.transformation }.getOr(TRTSTransformation.IDENTITY)
        val tex = { cube.map { it.textureOffset }.getOr(Vector2.ORIGIN) }
        val scale = { cube.map { it.textureSize }.getOr(Vector2.ORIGIN) }

        child(GroupTitle::class.java, GroupTitleProps("Edit Cube", state.on) { setState { copy(on = !on) } })

        child(TransformationInput::class, TransformationInputProps(
                usecase = "update.template.cube",
                transformation = trans,
                enable = cubeRef != ObjectRefNone
        ))

        div("Texture") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Texture", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("scale", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ tex().xf }, "tex.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ tex().yf }, "tex.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ scale().xf }, "tex.scale", cubeRef, vec2Of(185f, 20f))
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }
    }

    fun isSelectingOneCube(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    fun getObject(): Nullable<Pair<IObjectRef, IObjectCube>> {
        return props.access.modelSelection
                .flatMap {
                    if (isSelectingOneCube(props.access.model, it)) it.objects.first() else null
                }
                .flatMapNullable {
                    val cube = props.access.model.getObject(it).asNullable().flatMap { it as? IObjectCube }
                    it.asNullable().zip(cube)
                }
    }

    fun DivBuilder.valueInput(getter: () -> Float, cmd: String, cubeRef: IObjectRef, pos: IVector2) {
        child(FloatInput::class, FloatInputProps(
                getter = getter,
                command = "update.template.cube",
                metadata = mapOf("cube_ref" to cubeRef, "command" to cmd),
                enabled = cubeRef != ObjectRefNone,
                pos = pos)
        )
    }
}