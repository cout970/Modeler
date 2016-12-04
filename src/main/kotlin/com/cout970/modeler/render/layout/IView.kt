package com.cout970.modeler.render.layout

import com.cout970.modeler.ModelController
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.render.RenderManager
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/03.
 */
interface IView {

    fun loadResources(resourceManager: ResourceManager)

    fun getContentPanel(): Panel

    fun onLoad(renderManager: RenderManager)

    fun onRemove() {
    }

    fun renderExtras()

    var modelController: ModelController
}