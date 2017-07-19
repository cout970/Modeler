package com.cout970.modeler.view.gui.popup

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.toResourcePath
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JDialog

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
internal val textureExtensions: PointerBuffer = MemoryUtil.memAllocPointer(1).apply {
    put(MemoryUtil.memUTF8("*.png"))
    flip()
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

fun JDialog.center() {
    val toolkit = Toolkit.getDefaultToolkit()
    val x = (toolkit.screenSize.width - width) / 2
    val y = (toolkit.screenSize.height - height) / 2
    location = Point(x, y)
}
