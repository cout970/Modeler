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

    var project: Project = Project(Author(), "Unnamed").apply { creationTime = -1L }
        private set

    val guiState = GuiState()
    var world: World = World(emptyList(), Cursor(this))

    fun newProject(name: String, author: Author) {
        project = Project(author, name)
        world = World(listOf(project.model), Cursor(this))
    }

    fun saveProject(exportManager: ExportManager, path: String) {
        exportManager.saveProject(path, project)
    }

    fun loadProject(exportManager: ExportManager, path: String) {
        project = exportManager.loadProject(path)
        world = World(listOf(project.model), Cursor(this))
    }

    fun updateModel(model: IModel) {
        project.model = model
        world = World(listOf(model), Cursor(this))
    }
}