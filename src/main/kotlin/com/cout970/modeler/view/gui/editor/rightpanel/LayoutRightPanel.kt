package com.cout970.modeler.view.gui.editor.rightpanel

import com.cout970.modeler.view.gui.comp.module.ILayout

class LayoutRightPanel(val panel: RightPanel) : ILayout {

    override fun rescale() {
        panel.treeViewPanel.let {
            it.size.y = panel.size.y / 2f
            it.rescale()
        }
        panel.materialListPanel.let { it ->
            it.position.y = panel.size.y / 2
            it.size.y = panel.size.y / 2
            it.rescale()
        }
    }

    fun RightPanel.TreeViewPanel.rescale() {
        listPanel.let { list ->
            list.size.y = size.y - list.position.y
            list.container.childs
                    .map { it.size.y }
                    .sum()
                    .let { size ->
                        list.container.size.y = size
                    }
            list.resize()
        }
    }

    fun RightPanel.MaterialListPanel.rescale() {
        listPanel.let { list ->
            list.size.y = size.y - list.position.y
            list.container.childs
                    .map { it.position.y + it.size.y }
                    .max()
                    ?.let { size ->
                        list.container.size.y = size
                    }
            list.resize()
        }
    }
}