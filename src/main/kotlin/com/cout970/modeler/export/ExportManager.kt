package com.cout970.modeler.export

import com.cout970.modeler.log.print
import com.cout970.modeler.model.Material
import com.cout970.modeler.model.TexturedMaterial
import com.cout970.modeler.modeleditor.action.ActionImportModel
import com.cout970.modeler.modeleditor.action.ActionImportTexture
import com.cout970.modeler.project.Project
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.resource.toResourcePath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.awt.Color
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Created by cout970 on 2017/01/02.
 */
class ExportManager(val projectManager: ProjectManager, val resourceLoader: ResourceLoader) {

    val objImporter = ObjImporter()
    val objExporter = ObjExporter()
    val tcnImporter = TcnImporter()
    val jsonImporter = JsonImporter()
    val mcxExporter = McxExporter()

    val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(Material::class.java, MaterialSerializer())
            .create()!!

    init {
        projectManager.exportManager = this
    }

    fun loadProject(path: String): Project {
        val zip = ZipFile(path)
        val entry = zip.getEntry("project.json") ?: throw java.lang.IllegalStateException(
                "Missing file 'project.json' inside '$path'")

        val reader = zip.getInputStream(entry).reader()
        return gson.fromJson(JsonReader(reader), Project::class.java)
    }

    fun saveProject(path: String, project: Project) {
        val zip = ZipOutputStream(File(path).outputStream())
        val json = gson.toJson(project)
        zip.let {
            it.putNextEntry(ZipEntry("project.json"))
            it.write(json.toByteArray())
            it.closeEntry()
        }
        zip.close()
    }

    fun importModel(prop: ImportProperties) {
        val file = File(prop.path)
        when (prop.format) {
            ImportFormat.OBJ -> {
                projectManager.modelEditor.historyRecord.doAction(
                        ActionImportModel(projectManager.modelEditor, resourceLoader, prop.path) {
                            objImporter.import(file.toResourcePath(), prop.flipUV)
                        })
            }
            ImportFormat.TCN -> {
                projectManager.modelEditor.historyRecord.doAction(
                        ActionImportModel(projectManager.modelEditor, resourceLoader, prop.path) {
                            tcnImporter.import(file.toResourcePath())
                        })
            }
            ImportFormat.JSON -> {
                projectManager.modelEditor.historyRecord.doAction(
                        ActionImportModel(projectManager.modelEditor, resourceLoader, prop.path) {
                            jsonImporter.import(file.toResourcePath())
                        })
            }
        }
    }

    fun exportModel(prop: ExportProperties) {
        val file = File(prop.path)
        when (prop.format) {
            ExportFormat.OBJ -> {
                projectManager.modelEditor.addToQueue {
                    objExporter.export(file.outputStream(), projectManager.modelEditor.model, prop.materialLib)
                }
            }
            ExportFormat.MCX -> {
                projectManager.modelEditor.addToQueue {
                    mcxExporter.export(file.outputStream(), projectManager.modelEditor.model, prop.domain)
                }
            }
        }
    }

    fun importTexture(path: String) {
        projectManager.modelEditor.historyRecord.doAction(
                ActionImportTexture(projectManager.modelEditor, resourceLoader, path) { model ->
                    val file = File(path)
                    val material = TexturedMaterial(file.nameWithoutExtension, file.toResourcePath())
                    //TODO selected group
//                    val sel = SelectionGroup(model.getPaths(ModelPath.Level.GROUPS))
//                    model.applyGroup(sel) { group ->
//                        group.copy(material = material)
//                    }
                    model
                }
        )
    }

    fun exportTexture(path: String) {
        projectManager.modelEditor.addToQueue {
            try {
                //TODO use Texturizer
//                val file = File(path)
//                val group = projectManager.modelEditor.model.groups[0]
//                val size = group.material.size
//
//                val image = BufferedImage(size.xi, size.yi, BufferedImage.TYPE_INT_ARGB_PRE)
//                val g = image.createGraphics()
//                g.color = Color(0f, 0f, 0f, 0f)
//                g.fillRect(0, 0, size.xi, size.yi)
//
//                val set = mutableSetOf<Int>()
//
//                group.meshes.forEach { mesh ->
//                    mesh.getQuads().forEach { quad ->
//                        val a = quad.a.tex * size
//                        val b = quad.b.tex * size
//                        val c = quad.c.tex * size
//                        val d = quad.d.tex * size
//                        g.color = generateColor(set)
//                        g.fillPolygon(
//                                intArrayOf(
//                                        StrictMath.rint(a.xd).toInt(),
//                                        StrictMath.rint(b.xd).toInt(),
//                                        StrictMath.rint(c.xd).toInt(),
//                                        StrictMath.rint(d.xd).toInt()),
//                                intArrayOf(
//                                        StrictMath.rint(a.yd).toInt(),
//                                        StrictMath.rint(b.yd).toInt(),
//                                        StrictMath.rint(c.yd).toInt(),
//                                        StrictMath.rint(d.yd).toInt()), 4)
//                    }
//                }
//
//                ImageIO.write(image, "png", file)
            } catch (e: Exception) {
                e.print()
            }
        }
    }

    private fun generateColor(set: MutableSet<Int>): Color {
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