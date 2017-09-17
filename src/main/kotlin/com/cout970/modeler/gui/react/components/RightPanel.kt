package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.leguicomp.VerticalPanel
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedXFillY
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/16.
 */
class RightPanel private constructor() : ReactComponent<Unit, Unit>(Unit) {

    init {
        setState(Unit)
    }

    override fun render(): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        scalable = FixedXFillY(0f, 190f)
        setBorderless()

        listenerMap.addListener(EventModelUpdate::class.java) {
            setState(state)
        }

        panel {
            setBorderless()
            setTransparent()
            width = 190f
            height = 400f

            +FixedLabel("Model parts", 5f, 5f, 180f, 24f)
            +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 30f, 32f, 32f).also {
                it.setTooltip("Create Template Cube")
            }
            +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 30f, 32f, 32f).also {
                it.setTooltip("Create Cube Mesh")
            }
            +VerticalPanel(0f, 70f, 190f, 24f).also {
                context.gui.projectManager.model.objects.forEachIndexed { index, obj ->
                    +ModelObjectItem(ModelObjectProps(ObjectRef(index), obj.name))
                }
            }
        }
        panel {
            setTransparent()
            setBorderless()
            width = 190f
            height = 300f
            position.y = 400f

            +FixedLabel("Materials", 5f, 5f, 180f, 24f)
            +IconButton("material.view.import", "addTemplateCubeIcon", 5f, 30f, 32f, 32f)
            +VerticalPanel(0f, 70f, 190f, 24f).also {
                context.gui.projectManager.model.materials.forEachIndexed { index, mat ->
                    +ModelMaterialItem(ModelMaterialProps(MaterialRef(index), mat.name))
                }
            }
        }
    }

    companion object : IComponentFactory<Unit, Unit, RightPanel> {

        override fun createDefaultProps() = Unit

        override fun build(props: Unit): RightPanel = RightPanel()
    }
}