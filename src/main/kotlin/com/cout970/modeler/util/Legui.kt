package com.cout970.modeler.util

import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/07.
 */

fun inside(point: IVector2, pos: IVector2, size: IVector2): Boolean {
    return point.xd > pos.xd && point.xd < pos.xd + size.xd &&
            point.yd > pos.yd && point.yd < pos.yd + size.yd
}

val Component.absolutePosition: IVector2 get() {
    var sum = this.position.toIVector()
    var parent = this.parent
    while (parent != null) {
        sum += parent.position.toIVector()
        parent = parent.parent
    }
    return sum
}

fun <T : Component> T.onClick(id: Int, func: (Int) -> Unit): T {
    leguiEventListeners.addListener(MouseClickEvent::class.java, {
        if (it.action == MouseClickEvent.MouseClickAction.PRESS) {
            func(id)
        }
    })
    return this
}