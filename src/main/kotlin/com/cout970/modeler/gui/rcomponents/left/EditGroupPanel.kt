package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.gui.leguicomp.StringInput
import com.cout970.modeler.gui.leguicomp.alignAsColumn
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.gui.rcomponents.TransformationInput
import com.cout970.modeler.gui.rcomponents.TransformationInputProps
import com.cout970.modeler.util.disableInput
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.marginX
import com.cout970.reactive.dsl.postMount
import com.cout970.reactive.dsl.sizeY
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign

class EditGroupPanel : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditGroupPanel") {
        val groupRef = props.access.selectedGroup

        style {
            classes("left_panel_group", "edit_cube")
            height = if (state.on && groupRef != RootGroupRef) 457f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 16f)
        }

        val group = props.access.model.getGroup(groupRef)

        child(GroupTitle::class.java, GroupTitleProps("Edit Group", state.on) { setState { copy(on = !on) } })

        comp(StringInput("model.group.change.name")) {
            style {
                textState.horizontalAlign = HorizontalAlign.CENTER
                textState.text = group.name
                textState.fontSize = 24f
                sizeY = 32f
            }

            postMount {
                marginX(5f)
                this as StringInput

                if (groupRef == RootGroupRef) {
                    isEditable = false
                    isEnabled = false
                    disableInput()
                    this.textState.text = ""
                }
            }
        }

        child(TransformationInput::class, TransformationInputProps(
            usecase = "update.group.transform",
            transformation = group.transform,
            enable = groupRef != RootGroupRef
        ))

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
    }
}