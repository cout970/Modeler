package com.cout970.modeler.gui.editor.leftpanel.editcubepanel

import com.cout970.modeler.gui.comp.*

/**
 * Created by cout970 on 2017/07/16.
 */

class EditCubePanel : CPanel(width = 280f, height = 600f) {

    val sizePanel = CubeSizePanel()
    val posPanel = CubePosPanel()
    val rotationPanel = CubeRotationPanel()
    val rotationPosPanel = CubeRotationPosPanel()
    val textureOffsetPanel = CubeTextureOffsetPanel()

    init {
        add(sizePanel)
        add(posPanel)
        add(rotationPanel)
        add(rotationPosPanel)
        add(textureOffsetPanel)
        var p = 5f
        sizePanel.position.y = p
        p += 126f
        posPanel.position.y = p
        p += 126f
        rotationPanel.position.y = p
        p += 126f
        rotationPosPanel.position.y = p
        p += 126f
        textureOffsetPanel.position.y = p
        setBorderless()
    }

    class CubeSizePanel : CPanel(width = 280f, height = 90f) {
        val label = CLabel("Size", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }

        val sizeX = VariableInput("cube.size.x", 14f, 20f)
        val sizeY = VariableInput("cube.size.y", 103f, 20f)
        val sizeZ = VariableInput("cube.size.z", 192f, 20f)

        init {
            add(label)
            add(sizeX)
            add(sizeY)
            add(sizeZ)
            setBorderless()
            setTransparent()
        }
    }

    class CubePosPanel : CPanel(width = 280f, height = 90f) {
        val label = CLabel("Position", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
        val posX = VariableInput("cube.pos.x", 14f, 20f)
        val posY = VariableInput("cube.pos.y", 103f, 20f)
        val posZ = VariableInput("cube.pos.z", 192f, 20f)

        init {
            add(label)
            add(posX)
            add(posY)
            add(posZ)
            setBorderless()
            setTransparent()
        }
    }

    class CubeRotationPanel : CPanel(width = 280f, height = 90f) {
        val label = CLabel("Rotation", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
        val rotX = VariableInput("cube.rot.x", 14f, 20f)
        val rotY = VariableInput("cube.rot.y", 103f, 20f)
        val rotZ = VariableInput("cube.rot.z", 192f, 20f)

        init {
            add(label)
            add(rotX)
            add(rotY)
            add(rotZ)
            setBorderless()
            setTransparent()
        }
    }

    class CubeRotationPosPanel : CPanel(width = 280f, height = 90f) {
        val label = CLabel("Rot. Pivot", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
        val pivotX = VariableInput("cube.rot.pos.x", 14f, 20f)
        val pivotY = VariableInput("cube.rot.pos.y", 103f, 20f)
        val pivotZ = VariableInput("cube.rot.pos.z", 192f, 20f)

        init {
            add(label)
            add(pivotX)
            add(pivotY)
            add(pivotZ)
            setBorderless()
            setTransparent()
        }
    }

    class CubeTextureOffsetPanel : CPanel(width = 280f, height = 90f) {
        val label = CLabel("Tex. Offset", 0f, 0f, 280f, 18f).apply { textState.fontSize = 22f }
        val texX = VariableInput("cube.tex.x", 14f, 20f)
        val texY = VariableInput("cube.tex.y", 103f, 20f)

        init {
            add(label)
            add(texX)
            add(texY)
            setBorderless()
            setTransparent()
        }
    }
}