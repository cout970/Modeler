package com.cout970.modeler.gui.event

import com.cout970.glutilities.structure.Timer

data class Notification(
        val title: String,
        val text: String,
        val creationTime: Double = Timer.miliTime
)