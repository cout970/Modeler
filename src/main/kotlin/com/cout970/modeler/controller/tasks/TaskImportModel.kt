package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.toResourcePath
import java.io.File

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
                val newModel = import(state.exportManager)
                newModel.materials.forEach { it.loadTexture(state.resourceLoader) }
                modelCache = newModel
            } catch (e: Exception) {
                e.print()
                NotificationHandler.push(Notification("Error importing model",
                        "Error importing model at '${properties.path}': \n$e"))

            }
        }
        modelCache?.let {
            val oldModel = state.projectManager.model
            val newModel = if (properties.append) oldModel.merge(it) else it
            val newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, it.objectRefs)
            state.gui.state.tmpModel = null
            state.gui.state.hoveredObject = null
            state.gui.cursorManager.textureCursor = null
            state.gui.cursorManager.modelCursor = null
            state.projectManager.textureSelectionHandler.clear()
            state.projectManager.modelSelectionHandler.clear()

            state.gui.state.selectedMaterial = newModel.materialRefs.firstOrNull() ?: MaterialRefNone
            state.gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
            state.projectManager.modelSelectionHandler.setSelection(newSelection.asNullable())
            state.projectManager.updateModel(newModel)

            NotificationHandler.push(Notification("Model imported",
                    "Model at '${properties.path}' imported successfully"))
        }
    }

    override fun undo(state: Program) {
        state.projectManager.updateModel(oldModel)
        state.gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
    }

    fun import(exportManager: ExportManager): IModel {
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
            ImportFormat.PFF -> {
                exportManager.import(properties.path)
            }
        }
    }
}