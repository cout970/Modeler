package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.export.ProgramSave
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler

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
            val save = ProgramSave(ExportManager.CURRENT_SAVE_VERSION, properties, model, state.projectManager.materialPaths)
            exportManager.saveProject(path, save)

            log(Level.FINE) { "Saving done" }
            NotificationHandler.push(Notification("Project saved", "Project saved successfully"))
        } catch (e: Exception) {
            log(Level.ERROR) { "Unable to save project" }
            e.print()
            NotificationHandler.push(Notification("Error saving the project", "Unable to save the project to '$path': $e"))
        }
    }
}