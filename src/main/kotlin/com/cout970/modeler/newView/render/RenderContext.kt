package com.cout970.modeler.newView.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.IFormat
import com.cout970.glutilities.tessellator.ITessellator
import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.event.IInput
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.modeleditor.SelectionManager
import com.cout970.modeler.newView.Camera
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.util.Cache
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import org.lwjgl.opengl.GL14

/**
 * Created by cout970 on 2017/03/19.
 */
class RenderContext(
        val shaderHandler: ShaderHandler,
        val modelProvider: IModelProvider,
        val selectionManager: SelectionManager,
        val contentPanel: ContentPanel,
        val model: Model,
        val scene: Scene,
        val renderer: SceneRenderer,
        val input: IInput
) {

    val controllerState get() = contentPanel.controllerState
    val tessellator: Tessellator get() = shaderHandler.tessellator

    fun renderCache(cache: Cache<Int, VAO>, hash: Int, func: (Int) -> VAO) {
        shaderHandler.consumer.accept(cache.getOrCompute(hash, func))
    }

    fun draw(drawMode: Int, format: IFormat, hash: Int, func: ITessellator.() -> Unit) {
        renderCache(renderer.commonCache, hash) {
            shaderHandler.tessellator.compile(drawMode, format, func)
        }
    }

    fun draw(drawMode: Int, format: IFormat, hash: Int, camera: Camera, func: ITessellator.() -> Unit) {
        renderCache(renderer.volatileCache, hash xor camera.hashCode()) {
            shaderHandler.tessellator.compile(drawMode, format, func)
        }
    }

    fun draw(drawMode: Int, format: IFormat, func: ITessellator.() -> Unit) {
        shaderHandler.tessellator.draw(drawMode, format, shaderHandler.consumer, func)
    }

    fun ITessellator.setVec(slot: Int, data: IVector3): ITessellator {
        set(slot, data.x, data.y, data.z)
        return this
    }

    fun GLStateMachine.useBlend(amount: Float, func: () -> Unit) {
        blend.enable()
        blendFunc = GLStateMachine.BlendFunc.CONSTANT_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_CONSTANT_ALPHA
        GL14.glBlendColor(1f, 1f, 1f, amount)
        func()
        blendFunc = GLStateMachine.BlendFunc.SRC_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_SRC_ALPHA
        blend.disable()
    }

    fun Quad.tessellate(tes: ITessellator) {
        val norm = normal

        vertex.forEach { (pos, tex) ->
            tes.set(0, pos.x, pos.y, pos.z)
            tes.set(1, tex.xd, tex.yd)
            tes.set(2, norm.x, norm.y, norm.z)
            tes.endVertex()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RenderContext) return false

        if (modelProvider != other.modelProvider) return false
        if (selectionManager != other.selectionManager) return false
        if (contentPanel != other.contentPanel) return false
        if (model != other.model) return false
        if (scene != other.scene) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modelProvider.hashCode()
        result = 31 * result + selectionManager.hashCode()
        result = 31 * result + contentPanel.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + scene.hashCode()
        return result
    }
}