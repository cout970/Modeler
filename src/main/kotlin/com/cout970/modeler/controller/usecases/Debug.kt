package com.cout970.modeler.controller.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.Program
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.gui.editor.EditorPanel
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toIVector

/**
 * Created by cout970 on 2017/07/20.
 */

class Debug : IUseCase {
    override val key: String = "debug"

    lateinit var state: Program

    override fun createTask(): ITask {
        Debugger.debug {

            //            //reload gui
            gui.editorPanel = EditorPanel()
            gui.root.mainPanel = gui.editorPanel
            gui.editorPanel.gui = gui
            gui.editorPanel.update()
            gui.guiUpdater.initGui(gui)
            gui.guiUpdater.bindTextInputs(gui.editorPanel)
            gui.buttonBinder.bindButtons(gui.root.mainPanel!!)
            gui.root.mainPanel!!.bindProperties(gui.state)
            gui.root.updateSizes(gui.root.size.toIVector())
            gui.resources.reload(resourceLoader)
            gui.root.loadResources(gui.resources)
//
            //Test import
//            val prop = ImportProperties(
//                    "./electric_sieve.tcn",
//                    ImportFormat.TCN,
//                    false
//            )
//            taskHistory.processTask(TaskImportModel(projectManager.model, prop))
        }
        return TaskNone
    }
}