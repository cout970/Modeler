package com.cout970.modeler.newView

import com.cout970.modeler.event.EventController
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.modeler.newView.gui.Root
import com.cout970.modeler.newView.selector.Selector
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.resource.GuiResources
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.window.WindowHandler

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val projectManager: ProjectManager,
        val renderManager: RenderManager
) {

    lateinit var root: Root
    lateinit var contentPanel: ContentPanel
    lateinit var viewEventHandler: ViewEventHandler
    lateinit var buttonController: ButtonController
    lateinit var guiResources: GuiResources
    lateinit var selector: Selector

    val modelEditor: ModelEditor get() = projectManager.modelEditor

    fun init() {
        root = Root(this)
        renderManager.rootFrame = root
        contentPanel = root.centerPanel

        buttonController = ButtonController(projectManager, this)
        viewEventHandler = ViewEventHandler(contentPanel, eventController)
        selector = Selector(projectManager.modelEditor)
    }
}