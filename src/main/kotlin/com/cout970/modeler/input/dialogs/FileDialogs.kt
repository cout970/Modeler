package com.cout970.modeler.input.dialogs

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.util.toPointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter


object FileDialogs {

    fun saveFile(title: String, description: String, defaultPath: String, filters: List<String>): String? {
        return when (Config.fileDialog) {
            "Swing" -> swingSaveFile(description, defaultPath, filters)
            "NativeFileDialog" -> nfdSaveFile(defaultPath, filters)
            "TinyFileDialog" -> tfdSaveFile(title, description, defaultPath, filters)
            else -> tfdSaveFile(title, description, defaultPath, filters)
        }
    }

    fun openFile(title: String, description: String, filters: List<String>, defaultPath: String = ""): String? {
        return when (Config.fileDialog) {
            "Swing" -> swingOpenFile(description, filters, defaultPath)
            "NativeFileDialog" -> nfdOpenFile(filters, defaultPath)
            "TinyFileDialog" -> tfdOpenFile(title, description, filters, defaultPath)
            else -> tfdOpenFile(title, description, filters, defaultPath)
        }
    }

    private fun swingSaveFile(description: String, defaultPath: String, filters: List<String>): String? {
        val fc = JFileChooser()

        fc.addChoosableFileFilter(object : FileFilter() {

            override fun accept(f: File): Boolean {
                return filters.any { f.name.endsWith(it.replace("*.", "")) }
            }

            override fun getDescription(): String = description
        })

        fc.selectedFile = File(defaultPath)

        val returnVal = fc.showOpenDialog(null)

        return if (returnVal == JFileChooser.APPROVE_OPTION) fc.selectedFile.absolutePath else null
    }

    private fun nfdSaveFile(defaultPath: String, filters: List<String>): String? {
        val filterStr = filters.joinToString(",") { it.replace("*.", "") }
        val outPath = MemoryUtil.memAllocPointer(1)

        val res = NativeFileDialog.NFD_SaveDialog(filterStr, defaultPath, outPath)
        var path: String? = null

        if (res == NativeFileDialog.NFD_OKAY) {
            path = outPath.stringUTF8
            NativeFileDialog.nNFDi_Free(outPath.get(0))
        }

        MemoryUtil.memFree(outPath)

        return path
    }

    private fun tfdSaveFile(title: String, description: String, defaultPath: String, filters: List<String>): String? {
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


    private fun swingOpenFile(description: String, filters: List<String>, defaultPath: String): String? {
        val fc = JFileChooser()

        fc.addChoosableFileFilter(object : FileFilter() {

            override fun accept(f: File): Boolean {
                return filters.any { f.name.endsWith(it.replace("*.", "")) }
            }

            override fun getDescription(): String = description
        })

        fc.selectedFile = File(defaultPath)

        val returnVal = fc.showSaveDialog(null)

        return if (returnVal == JFileChooser.APPROVE_OPTION) fc.selectedFile.absolutePath else null
    }

    private fun nfdOpenFile(filters: List<String>, defaultPath: String): String? {

        val filterStr = filters.joinToString(",") { it.replace("*.", "") }
        val outPath = MemoryUtil.memAllocPointer(1)

        val res = NativeFileDialog.NFD_OpenDialog(filterStr, defaultPath, outPath)
        var path: String? = null

        if (res == NativeFileDialog.NFD_OKAY) {
            path = outPath.stringUTF8
            NativeFileDialog.nNFDi_Free(outPath.get(0))
        }

        MemoryUtil.memFree(outPath)

        return path
    }

    private fun tfdOpenFile(title: String, description: String, filters: List<String>, defaultPath: String): String? {

        val filtersBuffer = filters.toPointerBuffer()

        return try {
            TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, filtersBuffer, description, false)
        } catch (e: Throwable) {
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