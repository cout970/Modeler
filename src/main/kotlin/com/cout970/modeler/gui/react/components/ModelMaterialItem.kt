package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.leguicomp.TextButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.toColor
import com.cout970.vector.api.IVector2
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/16.
 */

class ModelMaterialItem private constructor(props: ModelMaterialProps) : ReactComponent<ModelMaterialProps, Boolean>(
        props) {

    init {
        setState(false)
    }

    override fun render(parentSize: IVector2): Component = panel {
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

    companion object : IComponentFactory<ModelMaterialProps, Boolean, ModelMaterialItem> {

        override fun createDefaultProps() = ModelMaterialProps(
                ref = MaterialRef(-1),
                name = "",
                index = 0,
                color = Config.colorPalette.lightDarkColor.toColor()
        )

        override fun build(props: ModelMaterialProps): ModelMaterialItem = ModelMaterialItem(props)
    }
}

data class ModelMaterialProps(val ref: IMaterialRef, val name: String, val index: Int, val color: Vector4f)