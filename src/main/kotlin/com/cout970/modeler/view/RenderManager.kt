package com.cout970.modeler.view

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.config.Config
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.gui.GuiRenderer
import com.cout970.modeler.view.scene.ModelSceneRenderer
import com.cout970.modeler.view.scene.SceneModel
import com.cout970.modeler.view.scene.SceneTexture
import com.cout970.modeler.view.scene.TextureSceneRenderer
import com.cout970.modeler.view.util.ShaderHandler
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    lateinit var uiManager: UIManager
    lateinit var window: GLFWWindow
    lateinit var guiRenderer: GuiRenderer
    lateinit var shaderHandler: ShaderHandler
    lateinit var timer: Timer
    lateinit var modelSceneRenderer: ModelSceneRenderer
    lateinit var textureSceneRenderer: TextureSceneRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, timer: Timer, window: GLFWWindow) {
        this.window = window
        this.timer = timer
        guiRenderer = GuiRenderer(uiManager.rootFrame, window.id)
        shaderHandler = ShaderHandler(resourceLoader)
        modelSceneRenderer = ModelSceneRenderer(shaderHandler)
        textureSceneRenderer = TextureSceneRenderer(shaderHandler)
        val c = Config.colorPalette.modelBackgroundColor
        GLStateMachine.clearColor = Color(c.xf, c.yf, c.zf)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
        uiManager.rootFrame.update()
    }

    override fun tick() {
        GLStateMachine.clear()
        uiManager.sceneController.scenes.forEach {
            when (it) {
                is SceneModel -> modelSceneRenderer.render(it)
                is SceneTexture -> textureSceneRenderer.render(it)
            }
        }
        guiRenderer.render(uiManager.rootFrame)
    }
}