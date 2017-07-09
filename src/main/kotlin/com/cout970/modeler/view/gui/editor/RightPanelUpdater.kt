package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.gui.ComponentUpdater

/**
 * Created by cout970 on 2017/07/08.
 */

class RightPanelUpdater : ComponentUpdater() {

    override fun onModelUpdate(old: IModel, new: IModel) {
        updateObjectList()
    }

    override fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        updateObjectList()
    }

    fun updateObjectList() {
        val tree = gui.editorPanel.rightPanel.treeViewPanel
        val materials = gui.editorPanel.rightPanel.materialListPanel
        val model = gui.actionExecutor.model
        val selection = gui.selectionHandler.getSelection()

        tree.listPanel.clearComponents()
        model.objects
                .mapIndexed { index, _ -> ObjectRef(index) }
                .forEach {
                    val name = model.objects[it.objectIndex].name
                    val item = RightPanel.ListItem(it, name)
                    if (model.isVisible(it)) {
                        item.showButton.hide()
                        item.hideButton.show()
                    } else {
                        item.showButton.show()
                        item.hideButton.hide()
                    }

                    item.position.y = tree.listPanel.components.size * item.size.y
                    tree.listPanel.addComponent(item)
                    if (selection?.isSelected(it) ?: false) {
                        item.backgroundColor = Config.colorPalette.selectedButton.toColor()
                    }
                    item.loadResources(gui.resources)
                }

        materials.listPanel.clearComponents()
        model.materials.forEach {
            val item = RightPanel.MaterialListItem(it)
            item.position.y = materials.listPanel.components.size * item.size.y
            materials.listPanel.addComponent(item)
            item.loadResources(gui.resources)
        }

        gui.commandExecutor.bindButtons(tree.listPanel)
        gui.commandExecutor.bindButtons(materials.listPanel)
    }
}