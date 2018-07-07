package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.animation.animationOf
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.input.dialogs.FileDialogs
import com.cout970.modeler.input.dialogs.MessageDialogs

/**
 * Created by cout970 on 2017/07/17.
 */

private var lastSaveFile: String? = null

@UseCase("project.new")
private fun newProject(gui: Gui, model: IModel, animation: IAnimation, properties: ProjectProperties): ITask = TaskAsync { returnFunc ->
    var accepts = true
    if (model.objects.isNotEmpty() || model.groupMap.isNotEmpty()) {
        accepts = MessageDialogs.warningBoolean(
                title = "New Project",
                message = "Do you want to create a new project? \n" +
                        "All unsaved changes will be lost!",
                default = false
        )
    }
    if (accepts) {
        openPopup<String>(gui, "project_name") { name ->
            val newProject = ProjectProperties(properties.owner, name)

            returnFunc(TaskUpdateProject(
                    oldProjectProperties = properties,
                    newProjectProperties = newProject,
                    oldModel = model,
                    newModel = Model.empty(),
                    oldAnimation = animation,
                    newAnimation = animationOf()
            ))
        }
    }
}

@UseCase("project.load")
private fun loadProject(properties: ProjectProperties, projectManager: ProjectManager,
                        exportManager: ExportManager): ITask = TaskAsync { returnFunc ->

    if (projectManager.model.objects.isNotEmpty()) {

        val result = MessageDialogs.warningBoolean(
                title = "Load Project",
                message = "Do you want to load a new project? \n" +
                        "All unsaved changes will be lost!",
                default = false
        )

        if (result) {
            askFileLocation()?.let { loadProjectWithoutAsking(it, exportManager, projectManager, returnFunc) }
        }
    } else {
        askFileLocation()?.let { loadProjectWithoutAsking(it, exportManager, projectManager, returnFunc) }
    }
}

private fun loadProjectWithoutAsking(file: String, exportManager: ExportManager, projectManager: ProjectManager,
                                     ret: (ITask) -> Unit) {
    lastSaveFile = file
    try {
        val save = exportManager.loadProject(file)
        ret.invoke(TaskUpdateProject(
                oldProjectProperties = projectManager.projectProperties,
                newProjectProperties = save.projectProperties,
                oldModel = projectManager.model,
                newModel = save.model,
                oldAnimation = projectManager.animation,
                newAnimation = save.animation
        ))
        NotificationHandler.push(Notification("Project loaded successfully", "Project loaded from '$file'"))
    } catch (e: Exception) {
        e.print()
        NotificationHandler.push(Notification("Error loading project", "Unable to load project at '$file': $e"))
    }
}

private fun askFileLocation(): String? {
    return FileDialogs.openFile(
            title = "Load Project",
            description = "Project File Format (*.pff)",
            filters = listOf("*.pff")
    )
}

@UseCase("project.save")
private fun saveProject(projectManager: ProjectManager, exportManager: ExportManager): ITask {

    val path = getSavePathOrAsk() ?: return TaskNone
    return TaskSaveProject(exportManager, path, projectManager.model, projectManager.projectProperties,
            projectManager.animation)
}

@UseCase("project.save.as")
private fun saveProjectAs(projectManager: ProjectManager, exportManager: ExportManager): ITask {

    val path = getSavePathOrAsk(true) ?: return TaskNone
    return TaskSaveProject(exportManager, path, projectManager.model, projectManager.projectProperties,
            projectManager.animation)
}

private fun getSavePathOrAsk(force: Boolean = false): String? {
    if (force || lastSaveFile == null) {
        val file = FileDialogs.saveFile(
                title = "Save As",
                description = "Project File Format (*.pff)",
                filters = listOf("*.pff"),
                defaultPath = ""
        )

        if (file != null) {
            lastSaveFile = if (file.endsWith(".pff")) file else "$file.pff"
        } else {
            return null
        }
    }
    return lastSaveFile
}