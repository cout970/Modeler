package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.selection.*

/**
 * Created by cout970 on 2017/06/21.
 */

data class Selection(
        override val selectionTarget: SelectionTarget,
        override val selectionType: SelectionType,
        val list: List<IRef>
) : ISelection {

    override fun isSelected(obj: IObjectRef): Boolean = obj in list
    override fun isSelected(obj: IFaceRef): Boolean = obj in list
    override fun isSelected(obj: IEdgeRef): Boolean = obj in list
    override fun isSelected(obj: IPosRef): Boolean = obj in list
}