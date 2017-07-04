package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.selector.Cursor
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.Project

/**
 * Created by cout970 on 2017/06/09.
 */
class ProjectController {


    val listeners = mutableListOf<(old: IModel, new: IModel) -> Unit>()
    var project: Project = Project(Author(), "Unnamed").apply { creationTime = -1L }
        private set

    private val modelList = mutableListOf<IModel>()

    var world: World = World(modelList, Cursor(this))

    fun newProject(name: String, author: Author) {
        project = Project(author, name)
        updateModel(project.model)
    }

    fun saveProject(exportManager: ExportManager, path: String) {
        exportManager.saveProject(path, project)
    }

    fun loadProject(exportManager: ExportManager, path: String) {
        project = exportManager.loadProject(path)
        updateModel(project.model)
    }

    fun updateModel(model: IModel) {
        val old = project.model
        project.model = model
        modelList.clear()
        modelList.add(project.model)
        world.lastModified = System.currentTimeMillis()
        listeners.forEach { it.invoke(old, model) }
    }
}