package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
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

    override fun run(state: Program) {
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
        modelCache?.let {
            state.projectManager.updateModel(it)
            state.gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
        }
    }

    override fun undo(state: Program) {
        state.projectManager.updateModel(oldModel)
        state.gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
    }

    fun import(): IModel {
        val file = File(properties.path).toResourcePath()
        return when (properties.format) {
            ImportFormat.OBJ -> {
                ModelImporters.objImporter.import(file, properties.flipUV)
            }
            ImportFormat.TCN -> {
                ModelImporters.tcnImporter.import(file)
            }
            ImportFormat.JSON -> {
                ModelImporters.jsonImporter.import(file)
            }
            ImportFormat.TBL -> {
                ModelImporters.tblImporter.import(file)
            }
            ImportFormat.MCX -> {
                ModelImporters.mcxImporter.import(file)
            }
        }
    }
}