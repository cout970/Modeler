package com.cout970.modeler.controller

import com.cout970.modeler.controller.tasks.TaskExportModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.BackupManager
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable

/**
 * Created by cout970 on 2017/07/28.
 */
class AutoRunner(
        val resourceLoader: ResourceLoader,
        val projectManager: ProjectManager,
        val exportManager: ExportManager,
        val processor: ITaskProcessor
) : ITickeable {

    var enableAutoExport = false
    var enableAutoImport = true
    var enableBackups = true

    private var lastHash = -1

    private val path = "./run/test.mcx"

    override fun tick() {

        if(enableBackups){
            BackupManager.update(Config.backupPath, exportManager, projectManager)
        }
        if (enableAutoExport) {
            Profiler.startSection("autoExport")
            if (projectManager.model.hashCode() != lastHash) {
                lastHash = projectManager.model.hashCode()
                log(Level.FINE) { "Exporting model" }

                processor.processTask(TaskExportModel(projectManager.model,
                        ExportProperties(path, ExportFormat.MCX, "", "magneticraft")
                ))
            }
            Profiler.endSection()
        }
        if (enableAutoImport) {
            Profiler.startSection("autoImport")
            val mat = projectManager.model.materials
            mat.filter { it.hasChanged() }.forEach { it.loadTexture(resourceLoader) }
            Profiler.endSection()
        }
    }
}