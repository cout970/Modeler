package com.cout970.modeler.view.gui.editor.rightpanel

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.GuiResources
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CLabel
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.CVerticalPanel
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.icon.ImageIcon

class RightPanel : CPanel() {

    val materialListPanel = MaterialListPanel()
    val treeViewPanel = TreeViewPanel()

    init {
        add(materialListPanel)
        add(treeViewPanel)
        materialListPanel.position.y = 400f
    }

    class TreeViewPanel : CPanel(width = 190f, height = 400f) {

        val titleLabel = CLabel("Model parts", 5f, 5f, 180f, 24f)
        val listPanel = CVerticalPanel(0f, 35f, 190f, 24f)

        init {
            add(titleLabel)
            add(listPanel)
        }
    }

    class MaterialListPanel : CPanel(width = 190f, height = 300f) {

        val titleLabel = CLabel("Materials", 5f, 5f, 180f, 24f)
        val listPanel = CVerticalPanel(0f, 35f, 190f, 24f)

        init {
            add(titleLabel)
            add(listPanel)
        }
    }

    class ListItem(val ref: IObjectRef, name: String) : CPanel(width = 176f, height = 24f) {

        val label = CLabel(name, 0f, 0f, 120f, 24f)
        val showButton = CButton("", 120f, 0f, 24f, 24f, "tree.view.show.item")
        val hideButton = CButton("", 120f, 0f, 24f, 24f, "tree.view.hide.item")
        val delButton = CButton("", 150f, 0f, 24f, 24f, "tree.view.delete.item")

        init {
            cornerRadius = 0f
            position.x = 1f
            backgroundColor = Config.colorPalette.primaryColor.toColor()
            add(label)
            add(hideButton)
            add(showButton)
            add(delButton)

            showButton.backgroundColor = ColorConstants.transparent()
            showButton.border.isEnabled = false

            hideButton.backgroundColor = ColorConstants.transparent()
            hideButton.border.isEnabled = false

            delButton.backgroundColor = ColorConstants.transparent()
            delButton.border.isEnabled = false
        }

        override fun loadResources(resources: GuiResources) {
            showButton.setImage(ImageIcon(resources.showIcon).also { it.size = Vector2f(24f) })
            hideButton.setImage(ImageIcon(resources.hideIcon).also { it.size = Vector2f(24f) })
            delButton.setImage(ImageIcon(resources.deleteIcon).also { it.size = Vector2f(18f) })
            super.loadResources(resources)
        }
    }

    class MaterialListItem(val ref: IMaterialRef, name: String) : CPanel(width = 180f, height = 24f) {

        val label = CLabel(name, 0f, 0f, 120f, 24f)
        val applyButton = CButton("", 120f, 0f, 24f, 24f, "material.view.apply")
        val loadButton = CButton("", 150f, 0f, 24f, 24f, "material.view.load")

        init {
            backgroundColor = Config.colorPalette.primaryColor.toColor()
            add(label)
            add(applyButton)
            add(loadButton)

            applyButton.backgroundColor = ColorConstants.transparent()
            applyButton.border.isEnabled = false

            loadButton.backgroundColor = ColorConstants.transparent()
            loadButton.border.isEnabled = false

            if (ref.materialIndex < 0) {
                loadButton.hide()
            }
        }

        override fun loadResources(resources: GuiResources) {
            applyButton.setImage(ImageIcon(resources.applyMaterial).apply { size = Vector2f(24f) })
            loadButton.setImage(ImageIcon(resources.loadMaterial).apply { size = Vector2f(20f) })
            super.loadResources(resources)
        }
    }
}