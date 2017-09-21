package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.selection.ClipboardNone.selection
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
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/16.
 */
class RightPanel private constructor() : ReactComponent<Unit, Unit>(Unit) {

    init {
        setState(Unit)
    }

    override fun render(parentSize: IVector2): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        posX = parentSize.xf - 190f
        FixedXFillY(190f).updateScale(this, parentSize)
        setBorderless()

        listenerMap.addListener(EventModelUpdate::class.java) {
            setState(state)
        }

        val materialOfSelectedObjects = mutableListOf<IMaterialRef>()
        val model = context.gui.projectManager.model

        panel {
            setBorderless()
            setTransparent()
            width = 190f
            height = parentSize.yf * 0.5f

            +FixedLabel("Model parts", 5f, 5f, 180f, 24f)
            +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 30f, 32f, 32f).also {
                it.setTooltip("Create Template Cube")
            }
            +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 30f, 32f, 32f).also {
                it.setTooltip("Create Cube Mesh")
            }
            +VerticalPanel(0f, 70f, 190f, height - 64f).apply {
                model.objectRefs.forEachIndexed { index, ref ->
                    val name = model.getObject(ref).name

                    val color = if (selection.isSelected(ref)) {
                        materialOfSelectedObjects += model.getObject(ref).material
                        Config.colorPalette.selectedButton.toColor()
                    } else {
                        Config.colorPalette.lightDarkColor.toColor()
                    }

                    +ModelObjectItem(ModelObjectProps(ref, name, model.isVisible(ref), color, index))
                }
                container.size.y = model.objectRefs.size * 24f
                resize()
            }
        }
        panel {
            setTransparent()
            setBorderless()

            width = 190f
            height = parentSize.yf * 0.5f
            position.y = parentSize.yf * 0.5f

            +FixedLabel("Materials", 5f, 5f, 180f, 24f)
            +IconButton("material.view.import", "addMaterialIcon", 5f, 30f, 32f, 32f)
            +IconButton("material.view.remove", "removeMaterialIcon", 45f, 30f, 32f, 32f)
            +VerticalPanel(0f, 70f, 190f, height - 64f).apply {

                (model.materialRefs + listOf(MaterialRef(-1))).forEachIndexed { index, ref ->
                    val name = model.getMaterial(ref).name

                    val color = when (ref) {
                        in materialOfSelectedObjects -> Config.colorPalette.greyColor.toColor()
                        context.gui.state.selectedMaterial -> Config.colorPalette.brightColor.toColor()
                        else -> Config.colorPalette.lightDarkColor.toColor()
                    }

                    +ModelMaterialItem(ModelMaterialProps(ref, name, index, color))
                }
                container.size.y = model.materialRefs.size * 24f + 24f
                resize()
            }
        }
    }

    companion object : IComponentFactory<Unit, Unit, RightPanel> {

        override fun createDefaultProps() = Unit

        override fun build(props: Unit): RightPanel = RightPanel()
    }
}