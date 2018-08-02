package com.cout970.modeler.gui

import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.gui.leguicomp.IResourceReloadable
import com.cout970.modeler.gui.views.IView
import com.cout970.modeler.util.isNotEmpty
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/05/14.
 */

class Root(val mainView: IView) : Frame(1f, 1f) {

    //@Inject
    lateinit var context: Context

    private fun updateView() {
        componentLayer.clearChildComponents()
        componentLayer.add(mainView.base)
    }

    fun updateSizes(newSize: IVector2) {
        updateView()
        size = newSize.toJoml2f()
        mainView.reBuild(newSize)
    }

    fun reRender() {
        updateSizes(size.toIVector())
    }

    fun loadResources(res: GuiResources) {
        recursiveLoadResources(mainView.base, res)
    }

    private fun recursiveLoadResources(it: Component, res: GuiResources) {
        if (it is IResourceReloadable) it.loadResources(res)
        if (it.isNotEmpty) it.childComponents?.forEach { recursiveLoadResources(it, res) }
    }

    fun bindButtons(buttonBinder: ButtonBinder) {
        buttonBinder.bindButtons(mainView.base)
    }
}