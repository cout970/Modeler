package com.cout970.modeler.modelcontrol

/**
 * Created by cout970 on 2016/12/07.
 */
interface ISelectable {

    fun canBeSelected(mode: SelectionMode): Boolean
}