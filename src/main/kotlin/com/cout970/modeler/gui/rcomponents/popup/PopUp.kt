package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.rcomponents.ConfigMenu
import com.cout970.modeler.gui.rcomponents.ConfigMenuProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.fill
import com.cout970.reactive.dsl.postMount
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style

data class PopUpProps(val state: GuiState, val propertyHolder: IProjectPropertiesHolder) : RProps
data class PopupReturnProps(val returnFunc: (Any?) -> Unit) : RProps

class PopUp : RStatelessComponent<PopUpProps>() {

    override fun RBuilder.render() = div("PopUp") {
        val popup = props.state.popup
        style {
            if (popup == null) {
                classes("popup_background_hide")
            } else {
                classes("popup_background")
            }
        }

        postMount {
            fill()
        }

        popup?.let {
            when (it.name) {
                "project_name" -> child(SelectNameDialog::class, PopupReturnProps(it.returnFunc))
                "import" -> child(ImportDialog::class, PopupReturnProps(it.returnFunc))
                "export" -> child(ExportDialog::class, PopupReturnProps(it.returnFunc))
                "export_texture" -> child(ExportTextureDialog::class, PopupReturnProps(it.returnFunc))
                "config" -> child(ConfigMenu::class, ConfigMenuProps(it.returnFunc, props.propertyHolder))
                "edit_texture" -> child(EditTexture::class, EditTextureProps(it.returnFunc, props.state, it.metadata))
            }
        }
    }
}