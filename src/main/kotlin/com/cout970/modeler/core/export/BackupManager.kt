package com.cout970.modeler.core.export

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.input.window.Loop
import java.io.File
import java.time.Instant


object BackupManager {

    const val WAIT_TIME = 5000
    var hash = -1
    var lastTick = Loop.currentTick

    fun update(path: String, exportManager: ExportManager, projectManager: ProjectManager){
        if(Loop.currentTick - lastTick > WAIT_TIME){
            lastTick = Loop.currentTick
            val modelHash = projectManager.model.hashCode()
            if(hash != modelHash){
                hash = modelHash
                try {
                    log(Level.NORMAL) { "Creating backup..." }
                    makeBackup(path, exportManager, projectManager)
                    log(Level.NORMAL) { "Backup done" }
                }catch (e: Exception){
                    log(Level.NORMAL) { "Backup error" }
                    e.print()
                }
            }
        }
    }

    private fun getBackupName(): String = "Backup_${Instant.now().toEpochMilli()}.pff"

    private fun makeBackup(path: String, exportManager: ExportManager, projectManager: ProjectManager){
        File(path).let { if(!it.exists()) it.mkdirs() }
        exportManager.saveProject("$path/${getBackupName()}", projectManager)
    }
}