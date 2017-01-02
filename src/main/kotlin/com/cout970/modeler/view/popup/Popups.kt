package com.cout970.modeler.view.popup

import com.cout970.modeler.export.ExportFormat
import com.cout970.modeler.modeleditor.ModelController
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
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

internal val importFileExtensions: PointerBuffer = MemoryUtil.memAllocPointer(3).apply {
    put(MemoryUtil.memUTF8("*.obj"))
    put(MemoryUtil.memUTF8("*.tcn"))
    put(MemoryUtil.memUTF8("*.json"))
    flip()
}

private val exportExtensionsObj: PointerBuffer = MemoryUtil.memAllocPointer(1).apply {
    put(MemoryUtil.memUTF8("*.obj"))
    flip()
}

fun getExportFileExtensions(format: ExportFormat): PointerBuffer {
    return exportExtensionsObj
}

fun showImportModelPopup(modelController: ModelController) {
    ImportDialog.show { (path, format) ->
        if (path != null) {
            modelController.exportManager.importModel(path, format!!)
        }
    }
}

fun showExportModelPopup(modelController: ModelController) {
    ExportDialog.show { (path, format) ->
        if (path != null) {
            modelController.exportManager.exportModel(path, format!!)
        }
    }
}

fun Missing(thing: String) {
    JOptionPane.showMessageDialog(null, "Operation not implemented yet: $thing")
}

fun JDialog.center() {
    val toolkit = Toolkit.getDefaultToolkit()
    val x = (toolkit.screenSize.width - width) / 2
    val y = (toolkit.screenSize.height - height) / 2
    location = Point(x, y)
}
