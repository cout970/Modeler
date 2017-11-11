package com.cout970.modeler.gui.components

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.gui.reactive.RBuildContext
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.leguicomp.IconButton
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.show
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/16.
 */
class ModelObjectItem : RComponent<ModelObjectProps, Boolean>() {

    init {
        state = false
    }

    override fun build(ctx: RBuildContext): Component = panel {

        backgroundColor = props.color
        cornerRadius = 0f
        width = 180f
        height = 24f
        posY = props.index * 24f
        setBorderless()

        +TextButton("tree.view.select", props.name, 0f, 0f, 120f, 24f).also {
            it.textState.horizontalAlign = HorizontalAlign.LEFT
            it.textState.padding.x = 10f
            it.setTransparent()
            it.metadata += "ref" to props.ref
        }
        +IconButton("tree.view.show.item", "showIcon", 120f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.setBorderless()
            it.metadata += "ref" to props.ref
            if (props.visible) it.hide() else it.show()
        }
        +IconButton("tree.view.hide.item", "hideIcon", 120f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.setBorderless()
            it.metadata += "ref" to props.ref
            if (!props.visible) it.hide() else it.show()
        }
        +IconButton("tree.view.delete.item", "deleteIcon", 150f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.setBorderless()
            it.metadata += "ref" to props.ref
        }
    }

    companion object : RComponentSpec<ModelObjectItem, ModelObjectProps, Boolean>
}

data class ModelObjectProps(
        val ref: IObjectRef,
        val name: String,
        val visible: Boolean,
        val color: Vector4f,
        val index: Float
)