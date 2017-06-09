package com.cout970.modeler.controller

import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.Project

/**
 * Created by cout970 on 2017/06/09.
 */
class ProjectController {

    var project: Project? = null
        private set

    fun newProject(name: String, author: Author) {
        project = Project(author, name)
    }

    fun saveProject() {

    }

    fun loadProject(exportManager: ExportManager, path: String) {
        project = exportManager.loadProject(path)
    }

    fun deleteProject() {

    }
}