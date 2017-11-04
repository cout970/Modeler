package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.injection.InjectFromGui
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.util.toPointerBuffer
import org.lwjgl.PointerBuffer
import org.lwjgl.util.tinyfd.TinyFileDialogs
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2017/07/17.
 */

private val saveFileExtension: PointerBuffer = listOf("*.pff").toPointerBuffer()
private var lastSaveFile: String? = null

class NewProject : IUseCase {

    override val key: String = "project.new"

    @Inject lateinit var model: IModel
    @Inject lateinit var properties: ProjectProperties

    @InjectFromGui("new.project.name")
    var projectName: String = "Unnamed"

    override fun createTask(): ITask {
        return TaskAsync {
            var accepts = true
            if (model.objects.isNotEmpty()) {
                val res = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to create a new project? \n" +
                        "All unsaved changes will be lost!"
                )
                accepts = res == JOptionPane.OK_OPTION
            }
            if (accepts) {
                val newProject = ProjectProperties(properties.owner, properties.name)
                it.invoke(TaskUpdateProject(properties, newProject, model, Model.empty()))
            }
        }
    }
}

class LoadProject : IUseCase {

    override val key: String = "project.load"

    @Inject lateinit var properties: ProjectProperties
    @Inject lateinit var projectManager: ProjectManager
    @Inject lateinit var exportManager: ExportManager

    override fun createTask(): ITask {
        if (projectManager.model.objects.isNotEmpty()) {
            val res = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to load a new project? \nAll unsaved changes will be lost!"
            )
            if (res != JOptionPane.OK_OPTION) return TaskNone
        }

        val file = TinyFileDialogs.tinyfd_openFileDialog("Load", "", saveFileExtension, "Project File Format (*.pff)",
                false)
        if (file != null) {
            lastSaveFile = file
            try {
                val save = exportManager.loadProject(file)
                projectManager.loadProjectProperties(save.projectProperties)
                projectManager.updateModel(save.model)

                return TaskUpdateProject(
                        oldProjectProperties = projectManager.projectProperties,
                        newProjectProperties = save.projectProperties,
                        oldModel = projectManager.model,
                        newModel = save.model
                )
            } catch (e: Exception) {
                e.print()
            }
        }
        return TaskNone
    }
}

class SaveProject : IUseCase {

    override val key: String = "project.save"

    @Inject lateinit var projectManager: ProjectManager
    @Inject lateinit var exportManager: ExportManager

    override fun createTask(): ITask {
        val path = getSavePathOrAsk() ?: return TaskNone
        return TaskSaveProject(exportManager, path, projectManager.model, projectManager.projectProperties)
    }
}

class SaveProjectAs : IUseCase {

    override val key: String = "project.save.as"

    @Inject lateinit var projectManager: ProjectManager
    @Inject lateinit var exportManager: ExportManager

    override fun createTask(): ITask {
        val path = getSavePathOrAsk(true) ?: return TaskNone
        return TaskSaveProject(exportManager, path, projectManager.model, projectManager.projectProperties)
    }
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