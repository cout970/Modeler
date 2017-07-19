package com.cout970.modeler.functional.tasks

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.resource.toResourcePath
import java.io.File
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2017/07/19.
 */
class TaskImportModel(
        val oldModel: IModel,
        val properties: ImportProperties
) : IUndoableTask {

    var modelCache: IModel? = null

    override fun run(state: ProgramState) {
        if (modelCache == null) {
            try {
                val newModel = import()
                newModel.materials.forEach { it.loadTexture(state.resourceLoader) }
                modelCache = newModel
            } catch(e: Exception) {
                e.print()
                JOptionPane.showMessageDialog(null, "Error importing model: \n$e")
            }
        }
        modelCache?.let { state.projectManager.updateModel(it) }
    }

    override fun undo(state: ProgramState) {
        state.projectManager.updateModel(oldModel)
    }

    fun import(): IModel {
        val file = File(properties.path)
        return when (properties.format) {
            ImportFormat.OBJ -> {
                ModelImporters.objImporter.import(file.toResourcePath(), properties.flipUV)
            }
            ImportFormat.TCN -> {
                ModelImporters.tcnImporter.import(file.toResourcePath())
            }
            ImportFormat.JSON -> {
                ModelImporters.jsonImporter.import(file.toResourcePath())
            }
            ImportFormat.TBL -> {
                ModelImporters.tblImporter.import(file.toResourcePath())
            }
        }
    }
}