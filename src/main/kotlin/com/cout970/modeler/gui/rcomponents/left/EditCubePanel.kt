package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.TRTSTransformation
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

        val pair = getObject()

        val trans = pair?.second?.transformation ?: TRTSTransformation.IDENTITY
        val tex = (pair?.second as? IObjectCube)?.textureOffset ?: Vector2.ORIGIN
        val scale = (pair?.second as? IObjectCube)?.textureSize ?: Vector2.ORIGIN

        child(GroupTitle::class.java, GroupTitleProps("Edit Cube", state.on) { setState { copy(on = !on) } })

        child(TransformationInput::class, TransformationInputProps(
                usecase = "update.template.cube",
                transformation = trans,
                enable = pair != null
        ))

        if (pair != null && pair.second is IObjectCube) {
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

                valueInput({ tex.xf }, "tex.x", pair.first, vec2Of(10f, 20f))
                valueInput({ tex.yf }, "tex.y", pair.first, vec2Of(98f, 20f))
                valueInput({ scale.xf }, "tex.scale", pair.first, vec2Of(185f, 20f))
            }
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }
    }

    fun isSelectingOne(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        model.getSelectedObjects(new).firstOrNull() ?: return false
        return true
    }

    fun getObject(): Pair<IObjectRef, IObject>? {
        val sel = props.access.modelSelection.getOrNull() ?: return null
        if (!isSelectingOne(props.access.model, sel)) return null
        val objRef = sel.objects.first()
        val obj = props.access.model.getObject(objRef)

        return objRef to obj
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