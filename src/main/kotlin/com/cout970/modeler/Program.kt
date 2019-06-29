package com.cout970.modeler

import com.cout970.modeler.controller.FutureExecutor
import com.cout970.modeler.controller.TaskHistory
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.input.window.WindowHandler
import com.cout970.modeler.render.RenderManager

/**
 * Created by cout970 on 2017/05/26.
 */

const val NAME = "Cout970's Modeler"

data class Program(
        val resourceLoader: ResourceLoader,
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val renderManager: RenderManager,
        val gui: Gui,
        val projectManager: ProjectManager,
        val mainLoop: Loop,
        val exportManager: ExportManager,
        val futureExecutor: FutureExecutor,
        val taskHistory: TaskHistory
)