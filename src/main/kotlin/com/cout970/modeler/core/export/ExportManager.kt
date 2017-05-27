package com.cout970.modeler.core.export

import com.cout970.modeler.core.model.material.IMaterial
import com.cout970.modeler.core.project.Project
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.awt.Color
import java.util.zip.ZipFile

/**
 * Created by cout970 on 2017/01/02.
 */
class ExportManager(val projectManager: ProjectManager, val resourceLoader: ResourceLoader) {

    //TODO
//    val objImporter = ObjImporter()
//    val objExporter = ObjExporter()
//    val tcnImporter = TcnImporter()
//    val jsonImporter = JsonImporter()
//    val mcxExporter = McxExporter()
//
    val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(IMaterial::class.java, MaterialSerializer())
            .create()!!

    //
//    init {
//        projectManager.exportManager = this
//    }
//
    fun loadProject(path: String): Project {
        val zip = ZipFile(path)
        val entry = zip.getEntry("project.json") ?: throw java.lang.IllegalStateException(
                "Missing file 'project.json' inside '$path'")

        val reader = zip.getInputStream(entry).reader()
        return gson.fromJson(JsonReader(reader), Project::class.java)
    }
//
//    fun saveProject(path: String, project: Project) {
//        val zip = ZipOutputStream(File(path).outputStream())
//        val json = gson.toJson(project)
//        zip.let {
//            it.putNextEntry(ZipEntry("project.json"))
//            it.write(json.toByteArray())
//            it.closeEntry()
//        }
//        zip.close()
//    }
//
//    fun importModel(prop: ImportProperties) {
//        val file = File(prop.path)
//        when (prop.format) {
//            ImportFormat.OBJ -> {
//                projectManager.modelEditor.historyRecord.doAction(
//                        ActionImportModel(projectManager.modelEditor, resourceLoader, prop.path) {
//                            objImporter.import(file.toResourcePath(), prop.flipUV)
//                        })
//            }
//            ImportFormat.TCN -> {
//                projectManager.modelEditor.historyRecord.doAction(
//                        ActionImportModel(projectManager.modelEditor, resourceLoader, prop.path) {
//                            tcnImporter.import(file.toResourcePath())
//                        })
//            }
//            ImportFormat.JSON -> {
//                projectManager.modelEditor.historyRecord.doAction(
//                        ActionImportModel(projectManager.modelEditor, resourceLoader, prop.path) {
//                            jsonImporter.import(file.toResourcePath())
//                        })
//            }
//        }
//    }
//
//    fun exportModel(prop: ExportProperties) {
//        val file = File(prop.path)
//        when (prop.format) {
//            ExportFormat.OBJ -> {
//                projectManager.modelEditor.addToQueue {
//                    objExporter.export(file.outputStream(), projectManager.modelEditor.model, prop.materialLib)
//                }
//            }
//            ExportFormat.MCX -> {
//                projectManager.modelEditor.addToQueue {
//                    mcxExporter.export(file.outputStream(), projectManager.modelEditor.model, prop.domain)
//                }
//            }
//        }
//    }
//
//
//    fun importTexture(path: String, selection: ElementSelection) {
//        projectManager.modelEditor.historyRecord.doAction(
//                ActionImportTexture(projectManager.modelEditor, resourceLoader, path) { model ->
//                    val file = File(path)
//                    val tex = TexturedMaterial(file.nameWithoutExtension, file.toResourcePath())
//
//                    val materials = model.resources.materials + tex
//                    val map = model.resources.pathToMaterial + selection.paths.associate { it to materials.size - 1 }
//                    val res = model.resources.copy(materials = materials, pathToMaterial = map)
//
//                    model.copy(resources = res)
//                }
//        )
//    }
//
//    fun exportTexture(path: String, material: IMaterial, selection: ElementSelection) {
//        projectManager.modelEditor.addToQueue {
//            try {
//                val file = File(path)
//                val model = projectManager.modelEditor.model
//                val size = material.size
//
//                val image = BufferedImage(size.xi, size.yi, BufferedImage.TYPE_INT_ARGB_PRE)
//                val g = image.createGraphics()
//                g.color = Color(0f, 0f, 0f, 0f)
//                g.fillRect(0, 0, size.xi, size.yi)
//
//                val set = mutableSetOf<Int>()
//
//                model.getLeafPaths()
//                        .filter { selection.isSelected(it) }
//                        .map { model.getElement(it) }
//                        .forEach { elem ->
//                            elem.getQuads().forEach { quad ->
//                                val a = quad.a.tex * size
//                                val b = quad.b.tex * size
//                                val c = quad.c.tex * size
//                                val d = quad.d.tex * size
//                                g.color = generateColor(set)
//                                g.fillPolygon(
//                                        intArrayOf(
//                                                StrictMath.rint(a.xd).toInt(),
//                                                StrictMath.rint(b.xd).toInt(),
//                                                StrictMath.rint(c.xd).toInt(),
//                                                StrictMath.rint(d.xd).toInt()),
//                                        intArrayOf(
//                                                StrictMath.rint(a.yd).toInt(),
//                                                StrictMath.rint(b.yd).toInt(),
//                                                StrictMath.rint(c.yd).toInt(),
//                                                StrictMath.rint(d.yd).toInt()), 4)
//                            }
//                        }
//
//                ImageIO.write(image, "png", file)
//            } catch (e: Exception) {
//                e.print()
//            }
//        }
//    }

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