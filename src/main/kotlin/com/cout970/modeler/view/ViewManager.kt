package com.cout970.modeler.view

import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.ITickeable
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.module.ModuleAddElement
import com.cout970.modeler.view.module.ModuleHistoric
import com.cout970.modeler.view.module.ModuleSelectionType
import com.cout970.modeler.view.module.ModuleTransform
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.scene.Scene

/**
 * Created by cout970 on 2016/12/27.
 */

class ViewManager : ITickeable {

    val root: RootFrame = RootFrame(this)
    lateinit var renderManager: RenderManager
    lateinit var window: GLFWWindow
    lateinit var sceneController: SceneController
    lateinit var moduleController: ModuleController

    fun init(renderManager: RenderManager, modelController: ModelController) {
        this.renderManager = renderManager
        window = renderManager.window
        sceneController = SceneController(this, modelController)
        sceneController.scenes += Scene(sceneController)
        moduleController = ModuleController(this, modelController)

        val modules = listOf(ModuleAddElement(moduleController), ModuleSelectionType(moduleController), ModuleTransform(moduleController), ModuleHistoric(moduleController))
        modules.forEach {
            root.leftBar.container.addComponent(it)
        }
        recalculateModules()
    }

    fun recalculateModules() {
        var last = 5f
        root.leftBar.container.components.forEach {
            it.position.y = last
            last = it.position.y + it.size.y + 5f
        }
        root.leftBar.container.size.y = last
    }

    override fun tick() {
        sceneController.update()
    }
}