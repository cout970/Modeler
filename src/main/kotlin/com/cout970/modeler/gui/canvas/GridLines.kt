package com.cout970.modeler.gui.canvas

import com.cout970.modeler.gui.Gui
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of

class GridLines {

    lateinit var gui: Gui

    var gridOffset: IVector3 = Vector3.ZERO
        set(value) { field = value; onChange() }

    var gridSize: IVector3 = vec3Of(16 * 5)
        set(value) { field = value; onChange() }

    var enableXPlane = false
        set(value) { field = value; onChange() }

    var enableYPlane = true
        set(value) { field = value; onChange() }

    var enableZPlane = false
        set(value) { field = value; onChange() }

    private fun onChange() {
        gui.state.gridLinesHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
    }
}