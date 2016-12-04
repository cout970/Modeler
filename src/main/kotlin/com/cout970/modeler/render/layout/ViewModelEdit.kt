package com.cout970.modeler.render.layout

import com.cout970.modeler.ModelController
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.render.ModelRenderer
import com.cout970.modeler.render.RenderManager
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/03.
 */
class ViewModelEdit : IView {

    override lateinit var modelController: ModelController
    lateinit var modelRender: ModelRenderer
    lateinit var renderManager: RenderManager
    val sidePanel = Panel()
    val modelPanel = Panel().apply { border.isEnabled = false; backgroundColor = Vector4f(0.73f, 0.9f, 1f, 1f) }
    private val contentPanel = Panel().apply { addComponent(sidePanel); addComponent(modelPanel) }

    override fun loadResources(resourceManager: ResourceManager) {
        modelRender = ModelRenderer(resourceManager)
    }

    override fun getContentPanel(): Panel = contentPanel

    override fun onLoad(renderManager: RenderManager) {
        this.renderManager = renderManager
    }

    override fun onRemove() {
    }

    override fun renderExtras() {
        sidePanel.position = Vector2f()
        sidePanel.size = Vector2f(200f, contentPanel.size.y)
        modelPanel.position = Vector2f(200f, 0f)
        modelPanel.size = Vector2f(contentPanel.size.x - 200f, contentPanel.size.y)
        val pos = modelPanel.position
        val size = modelPanel.size
        if (size.x < 1 || size.y < 1) return
        modelRender.render(modelController, vec2Of(pos.x, pos.y), vec2Of(size.x, size.y))
    }
}