package com.cout970.modeler.view

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.window.WindowHandler
import controller.CommandExecutor

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val projectManager: ProjectManager,
        val renderManager: RenderManager,
        val resourceLoader: ResourceLoader
//        val guiResources: GuiResources
) {

    lateinit var root: Root
    lateinit var guiUpdater: GuiUpdater
    lateinit var commandExecutor: CommandExecutor
    lateinit var listeners: Listeners
//    lateinit var eventListeners: EventListeners
//    lateinit var selector: Selector
//    lateinit var modelViewTarget: ModelViewTarget
//    lateinit var textureViewTarget: TextureViewTarget
//    lateinit var cameraUpdater: CameraUpdater

    fun init() {
        log(Level.FINE) { "[GuiInitializer] Initializing GUI" }

        log(Level.FINE) { "[GuiInitializer] Creating CommandExecutor" }
        commandExecutor = CommandExecutor()
        log(Level.FINE) { "[GuiInitializer] Creating GuiUpdater" }
        guiUpdater = GuiUpdater(this)
        root = guiUpdater.root
        renderManager.guiUpdater = guiUpdater

        log(Level.FINE) { "[GuiInitializer] Creating Listeners" }
        listeners = Listeners(eventController, guiUpdater)

//        log(Level.FINE) { "[GuiInitializer] Creating ContentPanel" }
//        contentPanel = ContentPanel()
//        log(Level.FINE) { "[GuiInitializer] Creating Gui root frame" }
//        root = Root(this, contentPanel)
//        log(Level.FINE) { "[GuiInitializer] Creating scene element selector" }
//        selector = Selector(projectManager.modelEditor, contentPanel, eventController)
//        log(Level.FINE) { "[GuiInitializer] Creating CameraUpdater" }
//        cameraUpdater = CameraUpdater(contentPanel.sceneHandler, eventController, windowHandler)
//
//        log(Level.FINE) { "[GuiInitializer] Creating ViewEventHandler" }
//        eventListeners = EventListeners(root, contentPanel, eventController, modelEditor, selector,
//                buttonController)
//        log(Level.FINE) { "[GuiInitializer] Creating ModelViewTarget" }
//        modelViewTarget = ModelViewTarget(modelEditor, contentPanel)
//        log(Level.FINE) { "[GuiInitializer] Creating TextureViewTarget" }
//        textureViewTarget = TextureViewTarget(modelEditor, contentPanel)
//        log(Level.FINE) { "[GuiInitializer] Adding primary scene" }
//        contentPanel.addScene(modelViewTarget)
        log(Level.FINE) { "[GuiInitializer] GUI Initialization done" }
    }
}