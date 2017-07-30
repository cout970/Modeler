package com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.gui.comp.*
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/07/16.
 */

class EditCubePanel : CPanel(width = 190f, height = 385f) {

    val editCubeLabel = CLabel("Edit cube", 5f, 5f, 180f, 24f)
    val sizePanel = CubeSizePanel()
    val posPanel = CubePosPanel()
    val rotationPanel = CubeRotationPanel()
    val rotationPosPanel = CubeRotationPosPanel()
    val textureOffsetPanel = CubeTextureOffsetPanel()

    init {
        add(editCubeLabel)
        add(sizePanel)
        add(posPanel)
        add(rotationPanel)
        add(rotationPosPanel)
        add(textureOffsetPanel)
        sizePanel.position.y = 30f
        posPanel.position.y = 105f
        rotationPanel.position.y = 180f
        rotationPosPanel.position.y = 255f
        textureOffsetPanel.position.y = 330f
        setBorderless()
        backgroundColor = Config.colorPalette.lightDarkColor.toColor()
    }

    class CubeSizePanel : CPanel(width = 190f, height = 75f) {
        val sizeXLabel = CLabel("Size X", 15f, 5f, 60f, 18f)
        val sizeXInput = CTextInput("cube.size.x", "0.0", 95f, 5f, 80f, 18f)
        val sizeYLabel = CLabel("Size Y", 15f, 28f, 60f, 18f)
        val sizeYInput = CTextInput("cube.size.y", "0.0", 95f, 28f, 80f, 18f)
        val sizeZLabel = CLabel("Size Z", 15f, 51f, 60f, 18f)
        val sizeZInput = CTextInput("cube.size.z", "0.0", 95f, 51f, 80f, 18f)

        init {
            add(sizeXLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(sizeXInput)
            add(sizeYLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(sizeYInput)
            add(sizeZLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(sizeZInput)
            setBorderless()
            setTransparent()
        }
    }

    class CubePosPanel : CPanel(width = 190f, height = 75f) {
        val posXLabel = CLabel("Position X", 15f, 5f, 60f, 18f)
        val posXInput = CTextInput("cube.pos.x", "0.0", 95f, 5f, 80f, 18f)
        val posYLabel = CLabel("Position Y", 15f, 28f, 60f, 18f)
        val posYInput = CTextInput("cube.pos.y", "0.0", 95f, 28f, 80f, 18f)
        val posZLabel = CLabel("Position Z", 15f, 51f, 60f, 18f)
        val posZInput = CTextInput("cube.pos.z", "0.0", 95f, 51f, 80f, 18f)

        init {
            add(posXLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posXInput)
            add(posYLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posYInput)
            add(posZLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posZInput)
            setBorderless()
            setTransparent()
        }
    }

    class CubeRotationPanel : CPanel(width = 190f, height = 175f) {
        val rotXLabel = CLabel("Rotation X", 15f, 5f, 60f, 18f)
        val rotXInput = CTextInput("cube.rot.x", "0.0", 95f, 5f, 80f, 18f)
        val rotYLabel = CLabel("Rotation Y", 15f, 28f, 60f, 18f)
        val rotYInput = CTextInput("cube.rot.y", "0.0", 95f, 28f, 80f, 18f)
        val rotZLabel = CLabel("Rotation Z", 15f, 51f, 60f, 18f)
        val rotZInput = CTextInput("cube.rot.z", "0.0", 95f, 51f, 80f, 18f)

        init {
            add(rotXLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(rotXInput)
            add(rotYLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(rotYInput)
            add(rotZLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(rotZInput)
            setBorderless()
            setTransparent()
        }
    }

    class CubeRotationPosPanel : CPanel(width = 190f, height = 75f) {
        val posXLabel = CLabel("Rot. Pivot X", 15f, 5f, 60f, 18f)
        val posXInput = CTextInput("cube.rot.pos.x", "0.0", 95f, 5f, 80f, 18f)
        val posYLabel = CLabel("Rot. Pivot Y", 15f, 28f, 60f, 18f)
        val posYInput = CTextInput("cube.rot.pos.y", "0.0", 95f, 28f, 80f, 18f)
        val posZLabel = CLabel("Rot. Pivot Z", 15f, 51f, 60f, 18f)
        val posZInput = CTextInput("cube.rot.pos.z", "0.0", 95f, 51f, 80f, 18f)

        init {
            add(posXLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posXInput)
            add(posYLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posYInput)
            add(posZLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posZInput)
            setBorderless()
            setTransparent()
        }
    }

    class CubeTextureOffsetPanel : CPanel(width = 190f, height = 75f) {
        val posXLabel = CLabel("Tex. Offset X", 15f, 5f, 60f, 18f)
        val posXInput = CTextInput("cube.tex.x", "0.0", 95f, 5f, 80f, 18f)
        val posYLabel = CLabel("Tex. Offset Y", 15f, 28f, 60f, 18f)
        val posYInput = CTextInput("cube.tex.y", "0.0", 95f, 28f, 80f, 18f)

        init {
            add(posXLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posXInput)
            add(posYLabel.apply { textState.horizontalAlign = HorizontalAlign.LEFT })
            add(posYInput)
            setBorderless()
            setTransparent()
        }
    }
}