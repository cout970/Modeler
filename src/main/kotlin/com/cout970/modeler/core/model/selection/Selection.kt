package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.selection.*

/**
 * Created by cout970 on 2017/06/21.
 */

data class Selection(
        override val selectionTarget: SelectionTarget,
        override val selectionType: SelectionType,
        override val refs: Set<IRef>
) : ISelection {

    constructor(selectionTarget: SelectionTarget, selectionType: SelectionType, list: List<IRef>)
            : this(selectionTarget, selectionType, list.toSet())

    override val size: Int get() = refs.size

    override fun isSelected(obj: IObjectRef): Boolean = obj in refs
    override fun isSelected(obj: IFaceRef): Boolean = obj in refs
    override fun isSelected(obj: IEdgeRef): Boolean = obj in refs
    override fun isSelected(obj: IPosRef): Boolean = obj in refs
}