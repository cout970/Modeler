package com.cout970.modeler.render.layout

import com.cout970.modeler.ResourceManager
import com.cout970.modeler.render.RenderManager
import com.cout970.modeler.render.controller.IViewController
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/03.
 */
abstract class Layout(val renderManager: RenderManager) {

    abstract val viewController: IViewController
    abstract val contentPanel: Panel

    open fun loadResources(resourceManager: ResourceManager) {

    }

    open fun onLoad() {
    }

    open fun onRemove() {
    }

    abstract fun renderExtras()
}