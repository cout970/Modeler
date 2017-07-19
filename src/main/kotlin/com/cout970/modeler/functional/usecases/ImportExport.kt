package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskCallback
import com.cout970.modeler.functional.tasks.TaskExportModel
import com.cout970.modeler.functional.tasks.TaskImportModel
import com.cout970.modeler.view.gui.popup.ExportDialog
import com.cout970.modeler.view.gui.popup.ImportDialog

/**
 * Created by cout970 on 2017/07/19.
 */

class ImportModel : IUseCase {

    override val key: String = "model.import"

    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        val callback = { returnCallback: (ITask) -> Unit ->
            ImportDialog.show { prop ->
                if (prop != null) {
                    returnCallback(TaskImportModel(model, prop))
                }
            }
        }
        return TaskCallback(callback)
    }
}

class ExportModel : IUseCase {

    override val key: String = "model.export"

    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        val callback = { returnCallback: (ITask) -> Unit ->
            ExportDialog.show { prop ->
                if (prop != null) {
                    returnCallback(TaskExportModel(model, prop))
                }
            }
        }
        return TaskCallback(callback)
    }
}