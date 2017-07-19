package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.Model

/**
 * Created by cout970 on 2017/07/19.
 */
interface IClipboard {
    val model: IModel
    val selection: ISelection
}

object ClipboardNone : IClipboard {
    override val model: IModel = Model.empty()
    override val selection: ISelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, emptyList())
}

data class Clipboard(
        override val model: IModel,
        override val selection: ISelection
) : IClipboard