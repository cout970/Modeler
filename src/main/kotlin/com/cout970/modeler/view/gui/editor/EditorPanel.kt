package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.gui.MutablePanel
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Label

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorPanel : MutablePanel() {

    val leftPanel = LeftPanel()
    val rightPanel = RightPanel()
    val centerPanel = CenterPanel()

    class CenterPanel : CPanel() {
        lateinit var canvasContainer: CanvasContainer
        val topMenu = CPanel()
        val canvasPanel = CanvasPanel()

        init {
            backgroundColor = ColorConstants.transparent()
            add(topMenu)
            add(canvasPanel)
        }
    }

    class CanvasPanel : CPanel() {
        val backgroundLabels: List<Label> = listOf(
                Label("Open new view:  Alt + N", 0f, 0f, 10f, 10f),
                Label("Close view:         Alt + D", 0f, 0f, 10f, 10f),
                Label("Resize view:       Alt + J/K", 0f, 0f, 10f, 10f)
        )

        init {
            backgroundColor = ColorConstants.transparent()
            backgroundLabels.forEach {
                add(it)
                it.textState.apply {
                    textColor = ColorConstants.white()
                    fontSize = 20f
                }
            }
        }
    }

    init {
        backgroundColor = ColorConstants.transparent()
        add(leftPanel)
        add(rightPanel)
        add(centerPanel)
    }

    override fun updateSizes(newSize: IVector2) {
        size = newSize.toJoml2f()
        leftPanel.let {
            it.size = Vector2f(190f, newSize.yf)
            it.position = Vector2f()
        }
        rightPanel.let {
            it.size = Vector2f(190f, newSize.yf)
            it.position = Vector2f(newSize.xf - 190f, 0f)
            it.treeViewPanel.let { panel ->
                panel.size.y = it.size.y / 2
                panel.listPanel.apply {
                    size.y = panel.size.y - position.y
                    container.childs.map { it.position.y + it.size.y }.max()?.let { size -> container.size.y = size }
                    resize()
                }
            }
            it.materialListPanel.let { panel ->
                panel.position.y = it.size.y / 2
                panel.size.y = it.size.y / 2
                panel.listPanel.apply {
                    size.y = panel.size.y - position.y
                    container.childs.map { it.position.y + it.size.y }.max()?.let { size -> container.size.y = size }
                    resize()
                }
            }
        }
        centerPanel.let {
            it.size = Vector2f(newSize.xf - (leftPanel.size.x + rightPanel.size.x), newSize.yf)
            it.position = Vector2f(leftPanel.size.x, 0f)

            it.topMenu.let { menu ->
                menu.size = Vector2f(it.size.x, 24f)
                menu.position = Vector2f()
            }
            it.canvasPanel.let { panel ->
                panel.size = Vector2f(it.size.x, it.size.y - 24f)
                panel.position = Vector2f(0f, 24f)
            }
            // show/hide help keybinds
            it.canvasContainer.layout.updateCanvas()
            if (it.canvasContainer.canvas.isEmpty()) {
                it.canvasPanel.backgroundLabels.forEachIndexed { index, label ->
                    label.show()
                    label.setPosition(it.size.x / 3f, (index - it.canvasPanel.backgroundLabels.size / 2) * 45f)
                    label.setSize(size.x - label.position.x, size.y - label.position.y)
                }
            } else {
                it.canvasPanel.backgroundLabels.forEach { it.hide() }
            }
        }
    }
}
