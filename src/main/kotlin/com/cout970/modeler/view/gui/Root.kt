package com.cout970.modeler.view.gui

import com.cout970.modeler.view.gui.search.SearchPanel
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/05/14.
 */

class Root : Frame(1f, 1f) {

    lateinit var searchPanel: SearchPanel
    lateinit var canvasPanel: Panel
    var backgroundLabels: List<Label> = listOf()

    fun refreshComponents() {
        clearComponents()
        addComponent(searchPanel)
        addComponent(canvasPanel)
        backgroundLabels.forEach { addComponent(it) }
    }
}