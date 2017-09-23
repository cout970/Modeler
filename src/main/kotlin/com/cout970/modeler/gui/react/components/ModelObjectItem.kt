package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.gui.comp.CButton
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.toColor
import com.cout970.vector.api.IVector2
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/16.
 */
class ModelObjectItem private constructor(props: ModelObjectProps) : ReactComponent<ModelObjectProps, Boolean>(props) {

    init {
        updateState(false)
    }

    override fun render(parentSize: IVector2): Component = panel {
        backgroundColor = props.color
        cornerRadius = 0f
        width = 182f
        height = 24f
        posY = props.index * 24f
        setBorderless()

        +CButton(props.name, 0f, 0f, 120f, 24f, "tree.view.select").also {
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

    companion object : IComponentFactory<ModelObjectProps, Boolean, ModelObjectItem> {

        override fun createDefaultProps() = ModelObjectProps(
                ref = ObjectRef(-1),
                name = "",
                visible = true,
                color = Config.colorPalette.lightDarkColor.toColor(),
                index = 0
        )

        override fun build(props: ModelObjectProps): ModelObjectItem = ModelObjectItem(props)
    }
}

data class ModelObjectProps(
        val ref: IObjectRef,
        val name: String,
        val visible: Boolean,
        val color: Vector4f,
        val index: Int
)