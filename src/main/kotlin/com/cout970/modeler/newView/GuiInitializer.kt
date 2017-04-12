package com.cout970.modeler.newView

import com.cout970.modeler.event.EventController
import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.modeler.newView.gui.Root
import com.cout970.modeler.newView.render.RenderManager
import com.cout970.modeler.newView.selector.Selector
import com.cout970.modeler.newView.viewtarget.ModelViewTarget
import com.cout970.modeler.newView.viewtarget.TextureViewTarget
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.resource.GuiResources
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.window.WindowHandler

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val projectManager: ProjectManager,
        val renderManager: RenderManager,
        val resourceLoader: ResourceLoader,
        val guiResources: GuiResources
) {

    lateinit var root: Root
    lateinit var contentPanel: ContentPanel
    lateinit var viewEventHandler: ViewEventHandler
    lateinit var buttonController: ButtonController
    lateinit var selector: Selector
    lateinit var modelViewTarget: ModelViewTarget
    lateinit var textureViewTarget: TextureViewTarget

    val modelEditor: ModelEditor get() = projectManager.modelEditor

    fun init() {
        log(Level.FINE) { "[GuiInitializer] Initializing GUI" }
        log(Level.FINE) { "[GuiInitializer] Creating ButtonController" }
        buttonController = ButtonController(projectManager, this)
        log(Level.FINE) { "[GuiInitializer] Creating ContentPanel" }
        contentPanel = ContentPanel()
        log(Level.FINE) { "[GuiInitializer] Creating Gui root frame" }
        root = Root(this, contentPanel)
        log(Level.FINE) { "[GuiInitializer] Creating scene element selector" }
        selector = Selector(projectManager.modelEditor, contentPanel, eventController)

        renderManager.rootFrame = root
        log(Level.FINE) { "[GuiInitializer] Creating ViewEventHandler" }
        viewEventHandler = ViewEventHandler(contentPanel, eventController, modelEditor, selector, buttonController)
        log(Level.FINE) { "[GuiInitializer] Creating ModelViewTarget" }
        modelViewTarget = ModelViewTarget(modelEditor)
        log(Level.FINE) { "[GuiInitializer] Creating TextureViewTarget" }
        textureViewTarget = TextureViewTarget(modelEditor)
        log(Level.FINE) { "[GuiInitializer] Adding primary scene" }
        contentPanel.addScene(modelViewTarget, modelEditor)
        log(Level.FINE) { "[GuiInitializer] GUI Initialization done" }
    }
}