package com.cout970.modeler.gui.views

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.vector.api.IVector2

interface IView {

    var gui: Gui

    val base: Panel

    fun reBuild(newSize: IVector2)
}