package com.cout970.modeler.functional.usecases

import com.cout970.modeler.ProgramState
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskNone

/**
 * Created by cout970 on 2017/07/20.
 */

class Debug : IUseCase {
    override val key: String = "debug"

    lateinit var state: ProgramState

    override fun createTask(): ITask {
        state.apply {

            //            //reload gui
//            gui.editorPanel = EditorPanel()
//            gui.root.mainPanel = gui.editorPanel
//            gui.guiUpdater.initGui(gui)
//            gui.root.updateSizes(gui.root.size.toIVector())
//
//            //Test import
//            val prop = ImportProperties(
//                    "./model.tbl",
//                    ImportFormat.TBL,
//                    false
//            )
//            taskHistory.processTask(TaskImportModel(projectManager.model, prop))
        }
        return TaskNone
    }
}