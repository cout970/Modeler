package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.*
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
            when (prop) {
                is ObjExportProperties -> {
                    ModelImporters.objExporter.export(file.outputStream(), model, state.gui, prop)
                }
                is McxExportProperties -> {
                    ModelImporters.mcxExporter.export(file.outputStream(), model, prop)
                }
                is GltfExportProperties -> {
                    ModelImporters.gltfExporter.export(file, model)
                }
                is VsExportProperties -> {
                    ModelImporters.vsExporter.export(file, model)
                }
            }
            pushNotification("Model exported successfully", "The model has been exported successfully to '${prop.path}'")
        } catch (e: Exception) {
            pushNotification("Error exporting model ", "Error: ${e.message}")
            e.print()
        }
    }
}