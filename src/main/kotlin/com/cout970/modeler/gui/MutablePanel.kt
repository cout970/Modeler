package com.cout970.modeler.gui

import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/06/09.
 */
abstract class MutablePanel : Panel() {

    abstract fun updateSizes(newSize: IVector2)
}