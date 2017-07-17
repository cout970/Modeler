package com.cout970.modeler.functional.usecases

import com.cout970.modeler.ProgramState
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.Project
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskNone
import com.cout970.modeler.functional.tasks.TaskUpdateProject
import org.liquidengine.legui.component.Component
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2017/07/17.
 */
class NewProjectUseCase : IUseCase<NewProjectEvent> {

    override val key = "project.new"
    override val processor = NewProjectProcessor()

    override fun buildEvent(state: ProgramState, caller: Component?): NewProjectEvent {
        return NewProjectEvent(
                project = state.projectManager.project,
                ask = state.projectManager.model.objects.isNotEmpty(),
                author = state.projectManager.project.owner,
                name = "Unnamed"
        )
    }
}

data class NewProjectEvent(
        val project: Project,
        val ask: Boolean,
        val author: Author,
        val name: String
) : IUserEvent

class NewProjectProcessor : IEventProcessor<NewProjectEvent> {

    override fun processEvent(event: NewProjectEvent): ITask {
        if (event.ask) {
            val res = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to create a new project? \n" +
                    "All unsaved changes will be lost!"
            )
            if (res != JOptionPane.OK_OPTION) return TaskNone
        }
        val newProject = Project(event.author, event.name)
        return TaskUpdateProject(event.project, newProject)
    }
}

