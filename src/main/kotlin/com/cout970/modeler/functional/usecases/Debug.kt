package com.cout970.modeler.functional.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.ProgramState
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskNone
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.gui.editor.EditorPanel

/**
 * Created by cout970 on 2017/07/20.
 */

class Debug : IUseCase {
    override val key: String = "debug"

    lateinit var state: ProgramState

    override fun createTask(): ITask {
        Debugger.debug {

            //            //reload gui
            gui.editorPanel = EditorPanel()
            gui.root.mainPanel = gui.editorPanel
            gui.guiUpdater.initGui(gui)
            gui.guiUpdater.bindTextInputs(gui.editorPanel)
            gui.buttonBinder.bindButtons(gui.root.mainPanel!!)
            gui.root.mainPanel!!.bindProperties(gui.state)
            gui.root.updateSizes(gui.root.size.toIVector())
            gui.resources.reload(resourceLoader)
            gui.root.mainPanel!!.loadResources(gui.resources)
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