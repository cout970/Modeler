package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.util.toPointerBuffer
import org.lwjgl.PointerBuffer
import org.lwjgl.util.tinyfd.TinyFileDialogs

/**
 * Created by cout970 on 2017/07/17.
 */

private val saveFileExtension: PointerBuffer = listOf("*.pff").toPointerBuffer()
private var lastSaveFile: String? = null

@UseCase("project.new")
fun newProject(model: IModel, properties: ProjectProperties): ITask = TaskAsync { returnFunc ->
    var accepts = true
    if (model.objects.isNotEmpty()) {
        accepts = TinyFileDialogs.tinyfd_messageBox(
                "New Project",
                "Do you want to create a new project? \nAll unsaved changes will be lost!",
                "okcancel",
                "warning",
                false
        )
    }
    if (accepts) {
        val newProject = ProjectProperties(properties.owner, properties.name)
        returnFunc(TaskUpdateProject(properties, newProject, model, Model.empty()))
    }
}

@UseCase("project.load")
fun loadProject(properties: ProjectProperties, projectManager: ProjectManager,
                exportManager: ExportManager): ITask = TaskAsync { returnFunc ->

    if (projectManager.model.objects.isNotEmpty()) {

        val result = TinyFileDialogs.tinyfd_messageBox(
                "Load Project",
                "Do you want to load a new project? \nAll unsaved changes will be lost!",
                "okcancel",
                "warning",
                false)

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
                newModel = save.model
        ))
    } catch (e: Exception) {
        e.print()
    }
}

private fun askFileLocation() = TinyFileDialogs.tinyfd_openFileDialog(
        "Load",
        "",
        saveFileExtension,
        "Project File Format (*.pff)",
        false)

@UseCase("project.save")
fun saveProject(projectManager: ProjectManager, exportManager: ExportManager): ITask {

    val path = getSavePathOrAsk() ?: return TaskNone
    return TaskSaveProject(exportManager, path, projectManager.model, projectManager.projectProperties)
}

@UseCase("project.save.as")
fun saveProjectAs(projectManager: ProjectManager, exportManager: ExportManager): ITask {

    val path = getSavePathOrAsk(true) ?: return TaskNone
    return TaskSaveProject(exportManager, path, projectManager.model, projectManager.projectProperties)
}

private fun getSavePathOrAsk(force: Boolean = false): String? {
    if (force || lastSaveFile == null) {
        val file = TinyFileDialogs.tinyfd_saveFileDialog("Save As", "", saveFileExtension,
                "Project File Format (*.pff)")
        if (file != null) {
            lastSaveFile = if (file.endsWith(".pff")) file else file + ".pff"
        } else {
            return null
        }
    }
    return lastSaveFile
}