package com.cout970.modeler.gui.comp

import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */
class Cache : Component() {

    val subComponents: MutableList<Component> = mutableListOf()
    val cache: MutableMap<String, Any> = mutableMapOf()
}