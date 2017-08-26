package com.cout970.modeler.gui

import com.cout970.modeler.gui.comp.CPanel
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/06/09.
 */
abstract class MutablePanel : CPanel() {

    abstract fun updateSizes(newSize: IVector2)
}