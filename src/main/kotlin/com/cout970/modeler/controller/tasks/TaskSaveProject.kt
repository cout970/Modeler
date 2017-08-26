package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectProperties

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskSaveProject(
        val exportManager: ExportManager,
        val path: String,
        val model: IModel,
        val properties: ProjectProperties
) : ITask {

    override fun run(state: Program) {
        try {
            log(Level.FINE) { "Saving project..." }
            exportManager.saveProject(path, model, properties)
            log(Level.FINE) { "Saving done" }
        } catch (e: Exception) {
            log(Level.ERROR) { "Unable to save project" }
            e.print()
        }
    }
}