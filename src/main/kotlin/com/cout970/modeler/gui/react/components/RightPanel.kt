package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.IModelAccessor
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedXFillY
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import com.cout970.vector.extensions.vec2Of
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
        val topSize = 85f

        if (props.hide) {
            hide()
        }

        +ModelStatistics { ModelStatistics.Props(props.modelAccessor) }

        panel {
            setBorderless()
            setTransparent()
            width = 190f
            height = (ctx.parentSize.yf - topSize) * 0.5f
            posY = topSize

            +FixedLabel("Model parts", 5f, 5f, 180f, 24f)
            +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 30f, 32f, 32f).also {
                it.setTooltip("Create Template Cube")
            }
            +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 30f, 32f, 32f).also {
                it.setTooltip("Create Cube Mesh")
            }

            +ModelObjectList { ModelObjectList.Props(props.modelAccessor, vec2Of(0f, 70f), vec2Of(190f, height - 72f)) }
        }
        panel {
            setBorderless()
            setTransparent()

            width = 190f
            height = (ctx.parentSize.yf - topSize) * 0.5f
            posY = topSize + (ctx.parentSize.yf - topSize) * 0.5f

            +FixedLabel("Materials", 5f, 5f, 180f, 24f)
            +IconButton("material.view.import", "addMaterialIcon", 5f, 30f, 32f, 32f)
            +IconButton("material.view.remove", "removeMaterialIcon", 45f, 30f, 32f, 32f)
            +ModelMaterialList {
                ModelMaterialList.Props(
                        modelAccessor = props.modelAccessor,
                        selectedMaterial = props.selectedMaterial,
                        pos = vec2Of(0f, 70f),
                        size = vec2Of(190f, height - 64f)
                )
            }
        }
    }

    class Props(val modelAccessor: IModelAccessor, val selectedMaterial: () -> IMaterialRef, val hide: Boolean)

    companion object : RComponentSpec<RightPanel, Props, Unit>
}