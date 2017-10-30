package com.cout970.modeler.gui

import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.gui.react.leguicomp.ToggleButton
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component

import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/05/14.
 */

class Root : Frame(1f, 1f) {

    //@Inject
    lateinit var context: Context

    var mainPanel: MutablePanel? = null
        set(value) {
            field = value
            container.clearChilds()
            container.add(value)
        }

    fun updateSizes(newSize: IVector2) {
        size = newSize.toJoml2f()
        mainPanel?.updateSizes(newSize)
    }

    fun loadResources(res: GuiResources) {
        mainPanel?.let { recursiveLoadResources(it, res) }
    }

    private fun recursiveLoadResources(it: Component, res: GuiResources) {
        if (it is IResourceReloadable) it.loadResources(res)
        (it as? Component)?.childs?.forEach { recursiveLoadResources(it, res) }
    }

    fun bindProperties(state: GuiState) {
        val properties = state.getBooleanProperties()
        recursiveBindProperties(mainPanel!!, properties)
    }

    private fun recursiveBindProperties(it: Component, properties: Map<String, IPropertyBind<Boolean>>) {
        when (it) {
            is ToggleButton -> it.bindProperties(properties)
            is Component -> it.childs.forEach { recursiveBindProperties(it, properties) }
        }
    }

    fun bindButtons(buttonBinder: ButtonBinder) {
        buttonBinder.bindButtons(mainPanel!!)
    }
}