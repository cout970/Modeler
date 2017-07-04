package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.api.model.IModel
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

        fun clear() {
            listPanel.clearComponents()
        }

        fun addItem(ref: IObjectRef, model: IModel, resources: GuiResources) {
            val name = model.objects[ref.objectIndex].name
            val item = ListItem(ref, name, resources)
            item.position.y = listPanel.components.size * item.size.y
            listPanel.addComponent(item)
        }
    }

    class MaterialListPanel : CPanel(width = 180f, height = 200f) {

        val titleLabel = CLabel("Materials", 5f, 5f, 180f, 24f)
        val listPanel = CPanel(0f, 35f, 180f, 700f)

        init {
            addComponent(titleLabel)
            addComponent(listPanel)
        }

        fun clear() {
            listPanel.clearComponents()
        }

        fun addItem(material: IMaterial, resources: GuiResources) {
            val item = MaterialListItem(material, resources)
            item.position.y = listPanel.components.size * item.size.y
            listPanel.addComponent(item)
        }
    }

    class ListItem(val ref: IObjectRef, name: String, resources: GuiResources) : CPanel(width = 180f, height = 24f) {

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
            showButton.setImage(ImageView(resources.showIcon).apply { size = Vector2f(24f) })
            delButton.backgroundColor = ColorConstants.transparent()
            delButton.border.isEnabled = false
            delButton.setImage(ImageView(resources.deleteIcon).apply { size = Vector2f(18f); position = Vector2f(3f) })
        }
    }

    class MaterialListItem(val material: IMaterial, resources: GuiResources) : CPanel(width = 180f, height = 24f) {

        val label = CLabel(material.name, 0f, 0f, 120f, 24f)

        init {
            backgroundColor = Config.colorPalette.primaryColor.toColor()
            addComponent(label)
        }
    }
}