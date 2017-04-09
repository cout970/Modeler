package com.cout970.modeler.newView

import com.cout970.modeler.event.EventController
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
        val resourceLoader: ResourceLoader
) {

    lateinit var root: Root
    lateinit var contentPanel: ContentPanel
    lateinit var viewEventHandler: ViewEventHandler
    lateinit var buttonController: ButtonController
    lateinit var guiResources: GuiResources
    lateinit var selector: Selector
    lateinit var modelViewTarget: ModelViewTarget
    lateinit var textureViewTarget: TextureViewTarget

    val modelEditor: ModelEditor get() = projectManager.modelEditor

    fun init() {
        buttonController = ButtonController(projectManager, this)
        guiResources = GuiResources(resourceLoader)
        contentPanel = ContentPanel()
        root = Root(this, contentPanel)
        selector = Selector(projectManager.modelEditor, contentPanel, eventController)

        renderManager.rootFrame = root
        viewEventHandler = ViewEventHandler(contentPanel, eventController)

        modelViewTarget = ModelViewTarget(modelEditor)
        textureViewTarget = TextureViewTarget(modelEditor)

        contentPanel.addScene(modelViewTarget, modelEditor)
    }
}