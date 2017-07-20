package com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel

import com.cout970.modeler.view.gui.comp.CLabel
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.CTextInput

/**
 * Created by cout970 on 2017/07/16.
 */

class EditCubePanel : CPanel(width = 190f, height = 400f) {

    val editCubeLabel = CLabel("Edit cube", 5f, 5f, 180f, 24f)
    val sizePanel = CubeSizePanel()
    val posPanel = CubePosPanel()
    val rotationPanel = CubeRotationPanel()

    init {
        add(editCubeLabel)
        add(sizePanel)
        add(posPanel)
        add(rotationPanel)
        sizePanel.position.y = 30f
        posPanel.position.y = 105f
        rotationPanel.position.y = 180f
    }

    class CubeSizePanel : CPanel(width = 190f, height = 75f) {
        val sizeXLabel = CLabel("Size x", 5f, 5f, 60f, 18f)
        val sizeXInput = CTextInput("cube.size.x", "0.0", 65f, 5f, 110f, 18f)
        val sizeYLabel = CLabel("Size y", 5f, 28f, 60f, 18f)
        val sizeYInput = CTextInput("cube.size.y", "0.0", 65f, 28f, 110f, 18f)
        val sizeZLabel = CLabel("Size z", 5f, 51f, 60f, 18f)
        val sizeZInput = CTextInput("cube.size.z", "0.0", 65f, 51f, 110f, 18f)

        init {
            add(sizeXLabel)
            add(sizeXInput)
            add(sizeYLabel)
            add(sizeYInput)
            add(sizeZLabel)
            add(sizeZInput)
            setBorderless()
        }
    }

    class CubePosPanel : CPanel(width = 190f, height = 75f) {
        val posXLabel = CLabel("Pos. x", 5f, 5f, 60f, 18f)
        val posXInput = CTextInput("cube.pos.x", "0.0", 65f, 5f, 110f, 18f)
        val posYLabel = CLabel("Pos. y", 5f, 28f, 60f, 18f)
        val posYInput = CTextInput("cube.pos.y", "0.0", 65f, 28f, 110f, 18f)
        val posZLabel = CLabel("Pos. z", 5f, 51f, 60f, 18f)
        val posZInput = CTextInput("cube.pos.z", "8", 65f, 51f, 110f, 18f)

        init {
            add(posXLabel)
            add(posXInput)
            add(posYLabel)
            add(posYInput)
            add(posZLabel)
            add(posZInput)
            setBorderless()
        }
    }

    class CubeRotationPanel : CPanel(width = 190f, height = 175f) {
        val rotXLabel = CLabel("Rot. x", 5f, 5f, 60f, 18f)
        val rotXInput = CTextInput("cube.rot.x", "0", 65f, 5f, 110f, 18f)
        val rotYLabel = CLabel("Rot. y", 5f, 28f, 60f, 18f)
        val rotYInput = CTextInput("cube.rot.y", "0", 65f, 28f, 110f, 18f)
        val rotZLabel = CLabel("Rot. z", 5f, 51f, 60f, 18f)
        val rotZInput = CTextInput("cube.rot.z", "0", 65f, 51f, 110f, 18f)
        val rotWLabel = CLabel("Rot. w", 5f, 74f, 60f, 18f)
        val rotWInput = CTextInput("cube.rot.w", "0", 65f, 74f, 110f, 18f)

        init {
            add(rotXLabel)
            add(rotXInput)
            add(rotYLabel)
            add(rotYInput)
            add(rotZLabel)
            add(rotZInput)
            add(rotWLabel)
            add(rotWInput)
            setBorderless()
        }
    }
}