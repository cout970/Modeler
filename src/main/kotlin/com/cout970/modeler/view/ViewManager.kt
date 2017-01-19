package com.cout970.modeler.view

import com.cout970.modeler.WindowController
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.module.*
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.scene.ModelScene
import com.cout970.modeler.view.scene.TextureScene
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2016/12/27.
 */

class ViewManager : ITickeable {

    val root: RootFrame = RootFrame(this)
    lateinit var renderManager: RenderManager
    lateinit var windowController: WindowController
    lateinit var sceneController: SceneController
    lateinit var moduleController: ModuleController

    lateinit var moduleStructure: ModuleStructure

    fun init(renderManager: RenderManager, modelController: ModelController, windowController: WindowController) {
        this.renderManager = renderManager
        this.windowController = windowController

        sceneController = SceneController(this, modelController)
        sceneController.scenes += ModelScene(sceneController)
//        sceneController.scenes += ModelScene(sceneController).apply { perspective = false; camera = camera.copy(angleX = 0.0, angleY = 0.0)  }
//        sceneController.scenes += ModelScene(sceneController).apply { perspective = false; camera = camera.copy(angleX = 0.0, angleY = -90.toRads())  }
//        sceneController.scenes += ModelScene(sceneController).apply { perspective = false; camera = camera.copy(angleX = 90.toRads(), angleY = 0.0)  }
        sceneController.scenes += TextureScene(sceneController)
        sceneController.refreshScenes()

        moduleController = ModuleController(this, modelController)

        moduleStructure = ModuleStructure(moduleController)
        val modules = listOf(ModuleAddElement(moduleController), ModuleSelectionType(moduleController),
                             ModuleTransform(moduleController), ModuleHistoric(moduleController), moduleStructure)

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
        moduleStructure.update()
        sceneController.update()
    }

    fun getSize(): IVector2 = windowController.window.getFrameBufferSize()
}