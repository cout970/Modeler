package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskAsync
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.Popup

class ShowConfig : IUseCase {

    override val key: String = "project.edit"

    @Inject lateinit var model: IModel
    @Inject lateinit var gui: Gui

    override fun createTask(): ITask {
        return TaskAsync {
            gui.state.popup = Popup("config") {
                gui.state.popup = null
                gui.root.reRender()
            }
            gui.root.reRender()
        }
    }
}