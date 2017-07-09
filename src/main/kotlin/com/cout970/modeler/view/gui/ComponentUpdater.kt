package com.cout970.modeler.view.gui

import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.view.Gui
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/07/08.
 */
abstract class ComponentUpdater {

    lateinit var gui: Gui

    open fun onModelUpdate(old: IModel, new: IModel) {

    }

    open fun onSelectionUpdate(old: ISelection?, new: ISelection?) {

    }

    open fun bindTextInputs(panel: Panel) {

    }

    open fun handleScroll(e: EventMouseScroll): Boolean {
        return false
    }
}