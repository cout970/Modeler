package com.cout970.modeler.view

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.gui.GuiRenderer
import com.cout970.modeler.view.scene.ModelScene
import com.cout970.modeler.view.scene.ModelSceneRenderer
import com.cout970.modeler.view.scene.TextureScene
import com.cout970.modeler.view.scene.TextureSceneRenderer
import com.cout970.modeler.view.util.ShaderHandler
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
        GLStateMachine.clearColor = Color(0.73f, 0.9f, 1f)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
        uiManager.rootFrame.update()
    }

    override fun tick() {
        GLStateMachine.clear()
        uiManager.sceneController.scenes.forEach {
            when (it) {
                is ModelScene -> modelSceneRenderer.render(it)
                is TextureScene -> textureSceneRenderer.render(it)
            }
        }
        guiRenderer.render(uiManager.rootFrame)
    }
}