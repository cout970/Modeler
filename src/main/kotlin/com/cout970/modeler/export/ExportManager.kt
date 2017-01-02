package com.cout970.modeler.export

import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.action.ActionImportModel
import java.io.File

/**
 * Created by cout970 on 2017/01/02.
 */
class ExportManager(val modelController: ModelController) {

    val objImporter = ObjImporter()
    val objExporter = ObjExporter()

    fun importModel(path: String, format: ImportFormat) {
        val file = File(path)
        when (format) {
            ImportFormat.OBJ -> {
                modelController.historyRecord.doAction(ActionImportModel(modelController, path) {
                    objImporter.import(file.inputStream())
                })
            }
            ImportFormat.TCN -> TODO()
            ImportFormat.JSON -> TODO()
        }
    }

    fun exportModel(path: String, format: ExportFormat) {
        val file = File(path)
        when (format) {
            ExportFormat.OBJ -> {
                modelController.addToQueue {
                    objExporter.export(file.outputStream(), modelController.model, "materials")
                }
            }
        }
    }
}