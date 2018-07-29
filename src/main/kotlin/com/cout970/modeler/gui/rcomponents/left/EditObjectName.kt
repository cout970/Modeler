package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.gui.leguicomp.StringInput
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.getOr
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign

class EditObjectName : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(false)

    override fun RBuilder.render() = div("EditObjectName") {
        style {
            classes("left_panel_group", "edit_obj_name")
            height = if (state.on) 64f + 4f else 24f
        }

        postMount {
            marginX(5f)
        }

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }

        val obj = getObject()
        val text = obj.map { it.name }.getOr("")

        child(GroupTitle::class.java, GroupTitleProps("Object name", state.on) { setState { copy(on = !on) } })

        comp(StringInput("model.obj.change.name")) {
            style {
                textState.horizontalAlign = HorizontalAlign.CENTER
                textState.text = text
                textState.fontSize = 24f
            }

            postMount {
                posY = 24f + 8f
                sizeY = 32f

                marginX(5f)

                this as StringInput

                obj.ifNull {
                    isEditable = false
                    isEnabled = false
                }

                metadata["obj"] = obj
            }
        }
    }

    private fun getObject(): Nullable<IObject> {
        val model = props.access.model
        val selection = props.access.modelSelection
        return selection
                .filter { it.size == 1 }
                .flatMap { it.refs.firstOrNull() }
                .filterIsInstance<IObjectRef>()
                .map { model.getObject(it) }
    }
}