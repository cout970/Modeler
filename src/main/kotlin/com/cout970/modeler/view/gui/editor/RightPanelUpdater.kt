package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toIVector
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

        val materialOfSelectedObjects = mutableListOf<IMaterialRef>()

        tree.listPanel.container.clearChilds()
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

                    item.position.y = tree.listPanel.container.childs.size * item.size.y
                    tree.listPanel.container.add(item)

                    if (selection?.isSelected(it) ?: false) {
                        item.backgroundColor = Config.colorPalette.selectedButton.toColor()
                        materialOfSelectedObjects += model.getObject(it).material
                    }
                    item.loadResources(gui.resources)
                }

        materials.listPanel.container.clearChilds()
        model.materials.forEachIndexed { index, it ->

            val item = RightPanel.MaterialListItem(MaterialRef(index), it.name)

            item.position.y = materials.listPanel.container.childs.size * item.size.y
            materials.listPanel.container.add(item)

            if (MaterialRef(index) in materialOfSelectedObjects) {
                item.backgroundColor = Config.colorPalette.selectedButton.toColor()
            }
            item.loadResources(gui.resources)
        }

        gui.commandExecutor.bindButtons(tree.listPanel)
        gui.commandExecutor.bindButtons(materials.listPanel)
        //Update scroll size
        gui.editorPanel.updateSizes(gui.editorPanel.size.toIVector())
    }
}