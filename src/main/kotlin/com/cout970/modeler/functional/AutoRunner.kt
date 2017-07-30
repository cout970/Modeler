package com.cout970.modeler.functional

import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.functional.tasks.TaskExportModel
import com.cout970.modeler.util.ITickeable

/**
 * Created by cout970 on 2017/07/28.
 */
class AutoRunner(
        val resourceLoader: ResourceLoader,
        val projectManager: ProjectManager,
        val processor: ITaskProcessor
) : ITickeable {

    var enableAutoExport = false
    var enableAutoImport = true

    private var lastHash = -1

    private val path = "./run/test.mcx"

    override fun tick() {
        if (enableAutoExport) {
            if (projectManager.model.hashCode() != lastHash) {
                lastHash = projectManager.model.hashCode()
                log(Level.FINE) { "Exporting model" }

                processor.processTask(TaskExportModel(projectManager.model,
                        ExportProperties(path, ExportFormat.MCX, "", "magneticraft")
                ))
            }
        }
        if (enableAutoImport) {
            val mat = projectManager.model.materials
            mat.filter { it.hasChanged() }.forEach { it.loadTexture(resourceLoader) }
        }
    }
}