package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.IMaterial
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.GuiResources
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CLabel
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.CToggleButton
import org.joml.Vector2f
import org.liquidengine.legui.component.ImageView
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/06/25.
 */
class RightPanel : CPanel() {

    val materialListPanel = MaterialListPanel()
    val treeViewPanel = TreeViewPanel()

    init {
        addComponent(materialListPanel)
        addComponent(treeViewPanel)
        treeViewPanel.position.y = 200f
    }

    class TreeViewPanel : CPanel(width = 180f, height = 700f) {

        val titleLabel = CLabel("Model parts", 5f, 5f, 180f, 24f)
        val listPanel = CPanel(0f, 35f, 180f, 700f)

        init {
            addComponent(titleLabel)
            addComponent(listPanel)
        }
    }

    class MaterialListPanel : CPanel(width = 180f, height = 200f) {

        val titleLabel = CLabel("Materials", 5f, 5f, 180f, 24f)
        val listPanel = CPanel(0f, 35f, 180f, 700f)

        init {
            addComponent(titleLabel)
            addComponent(listPanel)
        }
    }

    class ListItem(val ref: IObjectRef, name: String) : CPanel(width = 180f, height = 24f) {

        val label = CLabel(name, 0f, 0f, 120f, 24f)
        val showButton = CToggleButton(120f, 0f, 24f, 24f)
        val delButton = CButton("", 150f, 0f, 24f, 24f, "tree.view.delete.item")

        init {
            backgroundColor = Config.colorPalette.primaryColor.toColor()
            addComponent(label)
            addComponent(showButton)
            addComponent(delButton)

            showButton.backgroundColor = ColorConstants.transparent()
            showButton.border.isEnabled = false

            delButton.backgroundColor = ColorConstants.transparent()
            delButton.border.isEnabled = false
        }

        override fun loadResources(resources: GuiResources) {
            showButton.setImage(ImageView(resources.showIcon).apply { size = Vector2f(24f) })
            delButton.setImage(ImageView(resources.deleteIcon).apply { size = Vector2f(18f); position = Vector2f(3f) })
            super.loadResources(resources)
        }
    }

    class MaterialListItem(val material: IMaterial) : CPanel(width = 180f, height = 24f) {

        val label = CLabel(material.name, 0f, 0f, 120f, 24f)

        init {
            backgroundColor = Config.colorPalette.primaryColor.toColor()
            addComponent(label)
        }
    }
}