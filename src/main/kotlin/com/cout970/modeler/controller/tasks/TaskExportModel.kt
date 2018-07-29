package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.print
import com.cout970.modeler.gui.event.pushNotification
import java.io.File

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskExportModel(
        val model: IModel,
        val prop: ExportProperties
) : ITask {

    override fun run(state: Program) {
        val file = File(prop.path)
        try {
            when (prop.format) {
                ExportFormat.OBJ -> {
                    ModelImporters.objExporter.export(file.outputStream(), model, prop.materialLib)
                }
                ExportFormat.MCX -> {
                    ModelImporters.mcxExporter.export(file.outputStream(), model, prop.domain)
                }
                ExportFormat.GLTF -> {
                    ModelImporters.gltfExporter.export(file, model)
                }
            }
            pushNotification("Model exported successfully", "The model has been exported successfully to '${prop.path}'")
        } catch (e: Exception) {
            pushNotification("Error exporting model ", "Error: ${e.message}")
            e.print()
        }
    }
}