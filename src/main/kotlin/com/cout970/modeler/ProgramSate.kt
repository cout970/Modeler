package com.cout970.modeler

import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.window.Loop
import com.cout970.modeler.view.window.WindowHandler

/**
 * Created by cout970 on 2017/05/26.
 */
data class ProgramSate(
        val resourceLoader: ResourceLoader,
        val windowHandler: WindowHandler,
        val eventController: EventController,
        val projectManager: ProjectManager,
        val renderManager: RenderManager,
        val mainLoop: Loop,
        val exportManager: ExportManager,
        val guiState: GuiState
)