package com.cout970.modeler.render.layout

import com.cout970.modeler.ModelController
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.render.RenderManager
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/04.
 */
class ViewTextureMapping : IView {

    override lateinit var modelController: ModelController
    lateinit var renderManager: RenderManager

    override fun loadResources(resourceManager: ResourceManager) {
    }

    override fun getContentPanel(): Panel = Panel().apply { addComponent(Button(10f, 10f, 500f, 50f)) }

    override fun onLoad(renderManager: RenderManager) {
        this.renderManager = renderManager
    }

    override fun onRemove() {
    }

    override fun renderExtras() {
//        modelRender.render()
    }
}