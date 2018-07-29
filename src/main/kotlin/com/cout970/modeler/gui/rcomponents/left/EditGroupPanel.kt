package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.gui.leguicomp.alignAsColumn
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.gui.rcomponents.TransformationInput
import com.cout970.modeler.gui.rcomponents.TransformationInputProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.marginX
import com.cout970.reactive.dsl.postMount
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style

class EditGroupPanel : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditGroupPanel") {
        style {
            classes("left_panel_group", "edit_cube")
            height = if (state.on) 420f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 14f)
        }

        val groupRef = props.access.selectedGroup
        val group = props.access.model.getGroup(groupRef)

        child(GroupTitle::class.java, GroupTitleProps("Edit Group", state.on) { setState { copy(on = !on) } })

        child(TransformationInput::class, TransformationInputProps(
                usecase = "update.group.transform",
                transformation = group.transform,
                enable = groupRef != RootGroupRef
        ))

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
    }
}