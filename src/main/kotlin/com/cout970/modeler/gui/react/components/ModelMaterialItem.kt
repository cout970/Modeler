package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.gui.comp.CButton
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.toColor
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

    override fun render(): Component = panel {
        backgroundColor = Config.colorPalette.lightDarkColor.toColor()
        cornerRadius = 0f
        width = 182f
        height = 24f
        setBorderless()

        +CButton(props.name, 0f, 0f, 120f, 24f, "tree.view.select").also {
            it.textState.horizontalAlign = HorizontalAlign.LEFT
            it.textState.padding.x = 10f
        }
        +IconButton("tree.view.show.item", "showIcon", 120f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.border.isEnabled = false
        }
        +IconButton("tree.view.hide.item", "hideIcon", 120f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.border.isEnabled = false
        }
        +IconButton("tree.view.delete.item", "deleteIcon", 150f, 0f, 24f, 24f).also {
            it.setTransparent()
            it.border.isEnabled = false
        }
    }

    companion object : IComponentFactory<ModelMaterialProps, Boolean, ModelMaterialItem> {

        override fun createDefaultProps() = ModelMaterialProps(MaterialRef(-1), "")

        override fun build(props: ModelMaterialProps): ModelMaterialItem = ModelMaterialItem(props)
    }
}

data class ModelMaterialProps(val ref: IMaterialRef, val name: String)