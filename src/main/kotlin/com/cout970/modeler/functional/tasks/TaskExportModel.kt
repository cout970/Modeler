package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.core.export.ModelImporters
import java.io.File

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskExportModel(
        val model: IModel,
        val prop: ExportProperties
) : ITask {

    override fun run(state: ProgramState) {
        val file = File(prop.path)
        when (prop.format) {
            ExportFormat.OBJ -> {
                ModelImporters.objExporter.export(file.outputStream(), model, prop.materialLib)
            }
            ExportFormat.MCX -> {
                ModelImporters.mcxExporter.export(file.outputStream(), model, prop.domain)
            }
        }
    }
}