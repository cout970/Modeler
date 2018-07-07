package com.cout970.modeler.core.export

import com.cout970.modeler.PathConstants
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.util.createParentsIfNeeded
import com.google.common.collect.HashMultimap
import java.io.File
import java.nio.file.Files
import java.time.Instant


object BackupManager {

    private var hash = -1
    private var lastTick = System.currentTimeMillis()
    private var count = 0
    private val FILENAME_LEVEL_REGEX = """Backup_(\d+)_.*""".toRegex()

    fun update(path: String, exportManager: ExportManager, projectManager: ProjectManager) {
        val now = System.currentTimeMillis()

        // Wait for the time to make the backup
        if (now - lastTick > Config.backupInterval) {
            lastTick = now

            // Only make a new backup if there are changes in the model from the last backup
            val modelHash = projectManager.model.hashCode()
            if (hash != modelHash) {
                hash = modelHash

                val projectName = projectManager.projectProperties.name
                val finalPath = "$path/$projectName"

                try {
                    log(Level.NORMAL) { "Creating backup..." }
                    makeBackup(finalPath, getLevel(count), exportManager, projectManager)
                    count++
                    log(Level.NORMAL) { "Backup done" }
                } catch (e: Exception) {
                    log(Level.NORMAL) { "Backup error" }
                    e.print()
                }

                removeOldBackups(finalPath)
            }
        }
    }

    private fun removeOldBackups(path: String) {
        val folder = File(path)
        val files = folder.listFiles() ?: return

        val map = HashMultimap.create<Int, File>()

        files.forEach {
            val match = FILENAME_LEVEL_REGEX.matchEntire(it.nameWithoutExtension) ?: return@forEach
            val (group1) = match.destructured
            val level = group1.toInt()
            map.put(level, it)
        }

        removeIfNeeded(map[0], 5)
        removeIfNeeded(map[1], 5)
        removeIfNeeded(map[2], 5)
        removeIfNeeded(map[3], 5)
    }

    private fun removeIfNeeded(files: Collection<File>, max: Int) {
        if (files.size > max) {
            val toRemove = files.sortedBy { it.nameWithoutExtension }.take(files.size - max)
            toRemove.forEach {
                log(Level.FINE) { "Removing old Backup: ${it.name}" }
                it.delete()
            }
        }
    }

    private fun getLevel(count: Int): Int {
        return when {
            count % 60 == 0 -> 4
            count % 30 == 0 -> 3
            count % 15 == 0 -> 2
            count % 5 == 0 -> 1
            else -> 0
        }
    }

    private fun getBackupName(level: Int): String {
        return "Backup_${level}_${Instant.now().toString().replace("""[^a-zA-Z0-9.\-]""".toRegex(), "_")}.pff"
    }

    private fun makeBackup(path: String, level: Int, exportManager: ExportManager, projectManager: ProjectManager) {
        File(path).createParentsIfNeeded(true)
        val finalPath = "$path/${getBackupName(level)}"

        try {
            exportManager.saveProject(PathConstants.LAST_BACKUP_FILE_PATH, projectManager, false)
            Files.copy(File(PathConstants.LAST_BACKUP_FILE_PATH).toPath(), File(finalPath).toPath())
            log(Level.NORMAL) { "Backup saved at ${File(finalPath).absolutePath}" }
        } catch (e: Throwable) {
            e.print()
            log(Level.NORMAL) { "Backup error saving to file: ${File(finalPath).absolutePath}" }
        }
    }
}