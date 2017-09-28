package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.gui.react.event.EventMaterialUpdate
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.leguicomp.VerticalPanel
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedXFillY
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/16.
 */
class RightPanel : RComponent<RightPanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        posX = ctx.parentSize.xf - 190f
        FixedXFillY(190f).updateScale(this, ctx.parentSize)
        setBorderless()

        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state)
        }
        listenerMap.addListener(EventMaterialUpdate::class.java) {
            replaceState(state)
        }
        listenerMap.addListener(EventSelectionUpdate::class.java) {
            replaceState(state)
        }

        val materialOfSelectedObjects = mutableListOf<IMaterialRef>()
        val model = props.projectManager.model
        val selection = props.selectionHandler.getSelection()

        if (props.hide) {
            hide()
        }

        panel {
            setBorderless()
            setTransparent()
            width = 190f
            height = ctx.parentSize.yf * 0.5f

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

                    val color = if (selection?.isSelected(ref) == true) {
                        materialOfSelectedObjects += model.getObject(ref).material
                        Config.colorPalette.selectedButton.toColor()
                    } else {
                        Config.colorPalette.lightDarkColor.toColor()
                    }

                    +ModelObjectItem { ModelObjectProps(ref, name, model.isVisible(ref), color, index) }
                }
                container.size.y = model.objectRefs.size * 24f
                resize()
            }
        }
        panel {
            setTransparent()
            setBorderless()

            width = 190f
            height = ctx.parentSize.yf * 0.5f
            position.y = ctx.parentSize.yf * 0.5f

            +FixedLabel("Materials", 5f, 5f, 180f, 24f)
            +IconButton("material.view.import", "addMaterialIcon", 5f, 30f, 32f, 32f)
            +IconButton("material.view.remove", "removeMaterialIcon", 45f, 30f, 32f, 32f)
            +VerticalPanel(0f, 70f, 190f, height - 64f).apply {

                (model.materialRefs + listOf(MaterialRef(-1))).forEachIndexed { index, ref ->
                    val name = model.getMaterial(ref).name

                    val color = when (ref) {
                        in materialOfSelectedObjects -> Config.colorPalette.greyColor.toColor()
                        props.guiState.selectedMaterial -> Config.colorPalette.brightColor.toColor()
                        else -> Config.colorPalette.lightDarkColor.toColor()
                    }

                    +ModelMaterialItem { ModelMaterialItem.Props(ref, name, index, color) }
                }
                container.size.y = model.materialRefs.size * 24f + 24f
                resize()
            }
        }
    }

    class Props(val projectManager: ProjectManager, val selectionHandler: SelectionHandler, val guiState: GuiState,
                val hide: Boolean)

    companion object : RComponentSpec<RightPanel, Props, Unit>
}