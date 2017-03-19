package com.cout970.modeler.view

import com.cout970.modeler.event.EventHandler
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.gui.TextureHandler
import com.cout970.modeler.view.scene.Scene2d
import com.cout970.modeler.view.scene.Scene3d
import com.cout970.modeler.window.WindowHandler

/**
 * Created by cout970 on 2016/12/27.
 */
class UIManager(
        val windowHandler: WindowHandler,
        private val eventHandler: EventHandler,
        renderManager: RenderManager,
        private val textureHandler: TextureHandler,
        private val projectManager: ProjectManager) : ITickeable {

    val sceneController: SceneController
    val moduleController: ModuleController
    val buttonController: ButtonController

    val rootFrame: Root

    init {
        renderManager.uiManager = this
        buttonController = ButtonController(projectManager, this)
        rootFrame = Root(eventHandler, windowHandler, buttonController, textureHandler)
        sceneController = SceneController(projectManager.modelEditor, eventHandler, rootFrame, windowHandler.timer)
        moduleController = ModuleController(projectManager.modelEditor, rootFrame, buttonController, eventHandler,
                textureHandler)
        showScenes(0)
    }

    fun showScenes(layout: Int) {
        sceneController.scenes.clear()
        when (layout) {
            1 -> {
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
            }
            2 -> {
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
            }
            3 -> {
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene2d(projectManager.modelEditor, windowHandler, sceneController)
            }
            4 -> {
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene2d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
            }
            else -> {
                sceneController.scenes += Scene3d(projectManager.modelEditor, windowHandler, sceneController)
            }
        }
        sceneController.refreshScenes()
    }

    override fun preTick() {
        super.preTick()
        textureHandler.updateMaterials(projectManager.modelEditor.model)
    }

    override fun tick() {
        windowHandler.resetViewport()
        moduleController.tick()
        sceneController.tick()
    }
}