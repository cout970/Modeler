package com.cout970.modeler.gui.editor.rightpanel

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.gui.ComponentPresenter
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.toColor

/**
 * Created by cout970 on 2017/07/08.
 */

class RightPanelPresenter(
        val panel: RightPanel,
        val moduleRightPanel: ModuleRightPanel
) : ComponentPresenter() {

    override fun onModelUpdate(old: IModel, new: IModel) {
        updateObjectList()
    }

    override fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        updateObjectList()
    }

    fun updateObjectList() {
        val tree = panel.treeViewPanel
        val materials = panel.materialListPanel
        val model = gui.projectManager.model
        val selection = gui.selectionHandler.getSelection()

        val materialOfSelectedObjects = mutableListOf<IMaterialRef>()

        tree.listPanel.container.clearChilds()
        model.objectRefs.forEach { ref ->

            val name = model.getObject(ref).name
            val item = RightPanel.ListItem(ref, name)

            if (model.isVisible(ref)) {
                item.showButton.hide()
                item.hideButton.show()
            } else {
                item.showButton.show()
                item.hideButton.hide()
            }

            item.position.y = tree.listPanel.container.childs.size * item.size.y
            tree.listPanel.container.add(item)

            if (selection?.isSelected(ref) ?: false) {
                item.backgroundColor = Config.colorPalette.selectedButton.toColor()
                materialOfSelectedObjects += model.getObject(ref).material
            }

            item.loadResources(gui.resources)
        }

        materials.listPanel.container.clearChilds()
        (model.materialRefs + listOf(MaterialRef(-1))).forEach { ref ->

            val name = model.getMaterial(ref).name
            val item = RightPanel.MaterialListItem(ref, name)

            item.position.y = materials.listPanel.container.childs.size * item.size.y
            materials.listPanel.container.add(item)

            if (ref in materialOfSelectedObjects) {
                item.backgroundColor = Config.colorPalette.greyColor.toColor()
            }
            if (ref == gui.state.selectedMaterial) {
                item.backgroundColor = Config.colorPalette.brightColor.toColor()
            }

            item.loadResources(gui.resources)
        }

        gui.buttonBinder.bindButtons(tree.listPanel)
        gui.buttonBinder.bindButtons(materials.listPanel)
        //Update scroll size
        moduleRightPanel.layout.rescale()
    }
}