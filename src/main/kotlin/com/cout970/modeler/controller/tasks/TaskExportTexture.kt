package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.log.print
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.times
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by cout970 on 2017/07/24.
 */
class TaskExportTexture(
        val path: String,
        val size: IVector2,
        val model: IModel,
        val material: IMaterialRef
) : ITask {

    override fun run(state: Program) {
        try {
            val file = File(path)
            val set = mutableSetOf<Int>()
            val mesh = model.objectRefs
                    .map { model.getObject(it) }
                    .filter { it.visible && it.material == material }
                    .map { it.mesh }

            val image = BufferedImage(size.xi, size.yi, BufferedImage.TYPE_INT_ARGB_PRE)
            val g = image.createGraphics()

            g.color = Color(0f, 0f, 0f, 0f)
            g.fillRect(0, 0, size.xi, size.yi)

            mesh.forEach { m ->
                m.faces.forEach { face ->

                    val a = m.tex[face.tex[0]] * size
                    val b = m.tex[face.tex[1]] * size
                    val c = m.tex[face.tex[2]] * size
                    val d = m.tex[face.tex[3]] * size

                    g.color = generateColor(set)
                    g.fillPolygon(
                            intArrayOf(
                                    StrictMath.rint(a.xd).toInt(),
                                    StrictMath.rint(b.xd).toInt(),
                                    StrictMath.rint(c.xd).toInt(),
                                    StrictMath.rint(d.xd).toInt()),
                            intArrayOf(
                                    StrictMath.rint(a.yd).toInt(),
                                    StrictMath.rint(b.yd).toInt(),
                                    StrictMath.rint(c.yd).toInt(),
                                    StrictMath.rint(d.yd).toInt()),
                            4
                    )
                }
            }

            ImageIO.write(image, "png", file)
            NotificationHandler.push(Notification("Texture exported",
                    "Texture has been exported to '$path'"))
        } catch (e: Exception) {
            e.print()
            NotificationHandler.push(Notification("Error exporting texture",
                    "Error exporting texture to '$path': \n$e"))
        }
    }

    fun generateColor(set: MutableSet<Int>): Color {
        var rand = Math.random()
        if (set.size < 256) {
            while ((rand * 256).toInt() in set) {
                rand = Math.random()
            }
        }
        set.add((rand * 256).toInt())
        return Color.getHSBColor(rand.toFloat(), 0.5f, 1.0f)
    }
}