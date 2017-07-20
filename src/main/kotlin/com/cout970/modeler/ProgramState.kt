package com.cout970.modeler

import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.functional.FutureExecutor
import com.cout970.modeler.functional.TaskHistory
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.window.Loop
import com.cout970.modeler.view.window.WindowHandler

/**
 * Created by cout970 on 2017/05/26.
 */
data class ProgramState(
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