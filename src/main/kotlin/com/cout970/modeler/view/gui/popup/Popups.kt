package com.cout970.modeler.view.gui.popup

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.controller.ActionExecutor
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.project.Project
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.record.HistoricalRecord
import com.cout970.modeler.core.resource.toResourcePath
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JDialog
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2016/12/29.
 */

val popupImage = BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB).apply {
    for (i in 0..15) {
        for (j in 0..15) {
            setRGB(i, j, 0xFFFFFF)
        }
    }
}

internal val importFileExtensions: PointerBuffer = MemoryUtil.memAllocPointer(4).apply {
    put(MemoryUtil.memUTF8("*.obj"))
    put(MemoryUtil.memUTF8("*.tcn"))
    put(MemoryUtil.memUTF8("*.json"))
    put(MemoryUtil.memUTF8("*.tbl"))
    flip()
}
internal val textureExtensions: PointerBuffer = MemoryUtil.memAllocPointer(1).apply {
    put(MemoryUtil.memUTF8("*.png"))
    flip()
}

private val exportExtensionsObj: PointerBuffer = MemoryUtil.memAllocPointer(1).apply {
    put(MemoryUtil.memUTF8("*.obj"))
    flip()
}
private val exportExtensionsMcx: PointerBuffer = MemoryUtil.memAllocPointer(1).apply {
    put(MemoryUtil.memUTF8("*.mcx"))
    flip()
}

private val saveFileExtension: PointerBuffer = MemoryUtil.memAllocPointer(1).apply {
    put(MemoryUtil.memUTF8("*.pff"))
    flip()
}

private var lastSaveFile: String? = null

fun saveProject(projectManager: ProjectManager, exportManager: ExportManager) {
    if (lastSaveFile == null) {
        saveProjectAs(projectManager, exportManager)
    } else {
        saveProjectDirect(exportManager, projectManager.project, lastSaveFile!!)
    }
}

fun saveProjectAs(projectManager: ProjectManager, exportManager: ExportManager) {
    val file = TinyFileDialogs.tinyfd_saveFileDialog("Save As", "", saveFileExtension, "Project File Format (*.pff)")
    if (file != null) {
        lastSaveFile = if (file.endsWith(".pff")) file else file + ".pff"
        saveProjectDirect(exportManager, projectManager.project, lastSaveFile!!)
    }
}

fun newProject(projectManager: ProjectManager): Boolean {
    if (projectManager.model.objects.isNotEmpty()) {
        val res = JOptionPane.showConfirmDialog(null,
                "Do you want to create a new project? \nAll unsaved changes will be lost!")
        if (res != JOptionPane.OK_OPTION) return false
    }
    val author = projectManager.project.owner
    projectManager.loadProject(Project(author, "unnamed"))
    return true
}

fun loadProject(projectManager: ProjectManager, exportManager: ExportManager) {
    if (projectManager.model.objects.isNotEmpty()) {
        val res = JOptionPane.showConfirmDialog(null,
                "Do you want to load a new project? \nAll unsaved changes will be lost!")
        if (res != JOptionPane.OK_OPTION) return
    }

    val file = TinyFileDialogs.tinyfd_openFileDialog("Load", "", saveFileExtension, "Project File Format (*.pff)",
            false)
    if (file != null) {
        lastSaveFile = file
        try {
            val project = exportManager.loadProject(file)
            projectManager.loadProject(project)
        } catch (e: Exception) {
            e.print()
        }
    }
}

fun saveProjectDirect(exportManager: ExportManager, project: Project, path: String) {
    try {
        log(Level.FINE) { "Saving project..." }
        exportManager.saveProject(path, project)
        log(Level.FINE) { "Saving done" }
    } catch (e: Exception) {
        log(Level.ERROR) { "Unable to save project" }
        e.print()
    }
}

fun showImportModelPopup(exportManager: ExportManager, historyRecord: HistoricalRecord,
                         projectManager: ProjectManager) {

    ImportDialog.show { prop ->
        if (prop != null) {
            exportManager.importModel(prop, historyRecord, projectManager)
        }
    }
}

fun showExportModelPopup(exportManager: ExportManager, actionExecutor: ActionExecutor,
                         projectManager: ProjectManager) {
    ExportDialog.show { prop ->
        if (prop != null) {
            exportManager.exportModel(prop, actionExecutor, projectManager.model)
        }
    }
}

fun importTexture(projectManager: ProjectManager, materialRef: IMaterialRef? = null) {
    val file = TinyFileDialogs.tinyfd_openFileDialog("Import Texture", "",
            textureExtensions, "PNG texture (*.png)", false)
    if (file != null) {
        val archive = File(file)
        val mat = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
        if (materialRef != null) {
            projectManager.model.materials
            projectManager.updateMaterial(materialRef, mat)
        } else {
            projectManager.loadMaterial(mat)
        }
    }
}
//
//fun exportTexture(projectManager: ProjectController) {
//    val file = TinyFileDialogs.tinyfd_saveFileDialog("Export Texture", "texture.png",
//            textureExtensions, "PNG texture (*.png)")
//    if (file != null) {
//        val index = 0
//        val res = projectManager.modelEditor.model.resources
//        val mat: IMaterial
//        val paths: List<ElementPath>
//        if (res.materials.size > index) {
//            mat = res.materials[index]
//            paths = res.pathToMaterial
//                    .entries
//                    .filter { it.value == index }
//                    .map { it.key }
//                    .distinct()
//        } else {
//            mat = MaterialNone
//            paths = projectManager.modelEditor.model.getLeafPaths()
//        }
//        projectManager.exportManager.exportTexture(file, mat, ElementSelection(paths))
//    }
//}

fun Missing(thing: String) {
    JOptionPane.showMessageDialog(null, "Operation not implemented yet: $thing")
}

@Suppress("UNUSED_PARAMETER")
fun getExportFileExtensions(format: ExportFormat): PointerBuffer {
    return when (format) {
        ExportFormat.OBJ -> exportExtensionsObj
        ExportFormat.MCX -> exportExtensionsMcx
    }
}

fun JDialog.center() {
    val toolkit = Toolkit.getDefaultToolkit()
    val x = (toolkit.screenSize.width - width) / 2
    val y = (toolkit.screenSize.height - height) / 2
    location = Point(x, y)
}
