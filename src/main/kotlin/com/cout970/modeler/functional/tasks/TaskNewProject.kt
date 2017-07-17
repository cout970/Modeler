package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.core.project.Project

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateProject(val oldProject: Project, val newProject: Project) : IUndoableTask {

    override fun run(state: ProgramState) {
        state.projectManager.loadProject(newProject)
    }

    override fun undo(state: ProgramState) {
        state.projectManager.loadProject(oldProject)
    }
}