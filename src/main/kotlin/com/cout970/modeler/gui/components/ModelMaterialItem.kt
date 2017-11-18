package com.cout970.modeler.gui.components

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.leguicomp.IconButton
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/16.
 */

class ModelMaterialItem : RComponent<ModelMaterialItem.Props, Boolean>() {

    init {
        state = false
    }

    override fun build(ctx: RBuilder): Component = panel {
        backgroundColor = props.color
        cornerRadius = 0f
        width = 182f
        height = 24f
        position.y = props.index * 24f
        setBorderless()

        +TextButton("material.view.select", props.name, 0f, 0f, 120f, 24f).also {
            it.textState.horizontalAlign = HorizontalAlign.LEFT
            it.textState.padding.x = 10f
            it.setTransparent()
            it.metadata += "ref" to props.ref
        }
        if (props.ref.materialIndex != -1) {
            +IconButton("material.view.load", "loadMaterial", 120f, 0f, 24f, 24f).also {
                it.setTransparent()
                it.border.isEnabled = false
                it.metadata += "ref" to props.ref
            }
        }
        +IconButton("material.view.apply", "applyMaterial", 150f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.border.isEnabled = false
            it.metadata += "ref" to props.ref
        }
    }

    data class Props(val ref: IMaterialRef, val name: String, val index: Int, val color: Vector4f)

    companion object : RComponentSpec<ModelMaterialItem, Props, Boolean>
}

