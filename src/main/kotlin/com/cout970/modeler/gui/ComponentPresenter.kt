package com.cout970.modeler.gui

import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/07/08.
 */
abstract class ComponentPresenter {

    lateinit var gui: Gui


    open fun onModelUpdate(old: IModel, new: IModel) {

    }

    open fun onSelectionUpdate(old: ISelection?, new: ISelection?) {

    }

    open fun bindTextInputs(panel: Container<*>) {

    }

    open fun handleScroll(e: EventMouseScroll): Boolean {
        return false
    }
}