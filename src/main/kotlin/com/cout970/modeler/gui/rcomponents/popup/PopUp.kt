package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.rcomponents.ConfigMenu
import com.cout970.modeler.gui.rcomponents.ConfigMenuProps
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.joml.Vector4f

data class PopUpProps(val state: GuiState, val propertyHolder: IProjectPropertiesHolder) : RProps
data class PopupReturnProps(val returnFunc: (Any?) -> Unit) : RProps

class PopUp : RStatelessComponent<PopUpProps>() {

    override fun RBuilder.render() = div("PopUp") {
        val popup = props.state.popup
        style {
            if (popup == null) {
                hide()
            } else {
                backgroundColor { Vector4f(1f, 1f, 1f, 0.15f) }
            }
        }

        postMount {
            fill()
        }

        popup?.let {
            when (it.name) {
                "import" -> child(ImportDialog::class, PopupReturnProps(it.returnFunc))
                "export" -> child(ExportDialog::class, PopupReturnProps(it.returnFunc))
                "export_texture" -> child(ExportTextureDialog::class, PopupReturnProps(it.returnFunc))
                "config" -> child(ConfigMenu::class, ConfigMenuProps(it.returnFunc, props.propertyHolder))
            }
        }
    }
}