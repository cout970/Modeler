package com.cout970.modeler.input.dialogs

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.util.toPointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.tinyfd.TinyFileDialogs

object FileDialogs {

    fun saveFile(title: String, description: String, defaultPath: String, filters: List<String>): String? {

        val filtersBuffer = filters.toPointerBuffer()

        return try {
            TinyFileDialogs.tinyfd_saveFileDialog(title, defaultPath, filtersBuffer, description)
        } catch (e: Exception) {
            log(Level.ERROR) {
                "Error on a save file dialog: title='$title', description='$description', " +
                        "defaultPath='$defaultPath', filters=$filters"
            }
            e.print()
            null
        } finally {
            MemoryUtil.memFree(filtersBuffer)
        }
    }

    fun openFile(title: String, description: String, filters: List<String>, defaultPath: String = ""): String? {

        val filtersBuffer = filters.toPointerBuffer()

        return try {
            TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, filtersBuffer, description, false)
        } catch (e: Exception) {
            log(Level.ERROR) {
                "Error on a open file dialog: title='$title', description='$description', " +
                        "defaultPath='$defaultPath', filters=$filters"
            }
            e.print()
            null
        } finally {
            MemoryUtil.memFree(filtersBuffer)
        }
    }
}