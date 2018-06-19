package com.cout970.modeler.gui.leguicomp

import org.liquidengine.legui.component.SelectBox

/**
 * Created by cout970 on 2017/09/30.
 */
class DropDown(
        val x: Float = 0f,
        val y: Float = 0f,
        val width: Float = 10f,
        val height: Float = 10f
) : SelectBox(x, y, width, height) {

    val selectedIndex get() = elements.indexOf(selection)

    init {
//        selectionListPanel.verticalScrollBar.apply {
//            background { bright3 }
//            scrollColor = color { grey }
//            arrowColor = color { bright4 }
//        }

    }
}