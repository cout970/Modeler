package com.cout970.modeler.core.export

import com.cout970.modeler.PathConstants
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.util.createParentsIfNeeded
import java.io.File
import java.nio.file.Files
import java.time.Instant


object BackupManager {

    var hash = -1
    var lastTick = System.currentTimeMillis()

    fun update(path: String, exportManager: ExportManager, projectManager: ProjectManager) {
        val now = System.currentTimeMillis()

        if (now - lastTick > Config.backupInterval) {
            lastTick = now
            val modelHash = projectManager.model.hashCode()
            if (hash != modelHash) {
                hash = modelHash
                try {
                    log(Level.NORMAL) { "Creating backup..." }
                    makeBackup(path, exportManager, projectManager)
                    log(Level.NORMAL) { "Backup done" }
                } catch (e: Exception) {
                    log(Level.NORMAL) { "Backup error" }
                    e.print()
                }
            }
        }
    }

    private fun getBackupName(projectName: String): String {
        return "Backup_${projectName}_${Instant.now().toString().replace("""[^a-zA-Z0-9.\-]""".toRegex(), "_")}.pff"
    }

    private fun makeBackup(path: String, exportManager: ExportManager, projectManager: ProjectManager) {
        val projectName = projectManager.projectProperties.name
        File("$path/$projectName").createParentsIfNeeded(true)
        val finalPath = "$path/$projectName/${getBackupName(projectName)}"

        exportManager.saveProject(PathConstants.LAST_BACKUP_FILE_PATH, projectManager)
        Files.copy(File(PathConstants.LAST_BACKUP_FILE_PATH).toPath(), File(finalPath).toPath())
        log(Level.NORMAL) { "Backup saved at ${File(finalPath).absolutePath}" }
    }
}