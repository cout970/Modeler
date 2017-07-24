package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.Gson
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
class ExportManager(val resourceLoader: ResourceLoader) {

    val gson = GsonBuilder()
            .setExclusionStrategies(ProjectExclusionStrategy())
            .setPrettyPrinting()
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(IMaterial::class.java, MaterialSerializer())
            .registerTypeAdapter(IModel::class.java, ModelSerializer())
            .registerTypeAdapter(IObject::class.java, ObjectSerializer())
            .registerTypeAdapter(IMesh::class.java, MeshSerializer())
            .registerTypeAdapter(IFaceIndex::class.java, FaceSerializer())
            .registerTypeAdapter(ITransformation::class.java, TransformationSerializer())
            .registerTypeAdapter(IMaterialRef::class.java, MaterialRefSerializer())
            .create()!!

    fun loadProject(path: String): Pair<IModel, ProjectProperties> {
        val zip = ZipFile(path)

        val properties = zip.load<ProjectProperties>("project.json", gson) ?:
                         throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson) ?:
                    throw IllegalStateException("Missing file 'model.json' inside '$path'")

        return model to properties
    }

    inline fun <reified T> ZipFile.load(entryName: String, gson: Gson): T? {
        val entry = getEntry(entryName) ?: return null
        val reader = getInputStream(entry).reader()
        return gson.fromJson(JsonReader(reader), T::class.java)
    }

    fun saveProject(path: String, model: IModel, properties: ProjectProperties) {
        val zip = ZipOutputStream(File(path).outputStream())
        zip.let {
            it.putNextEntry(ZipEntry("project.json"))
            it.write(gson.toJson(properties).toByteArray())
            it.closeEntry()
            it.putNextEntry(ZipEntry("model.json"))
            it.write(gson.toJson(model).toByteArray())
            it.closeEntry()
        }
        zip.close()
    }

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

    fun loadLastProjectIfExists(projectManager: ProjectManager) {
        val path = File("./saves/last.pff")
        if (path.exists()) {
            try {
                log(Level.FINE) { "Found last project, loading..." }
                val (model, properties) = loadProject(path.path)
                projectManager.loadProjectProperties(properties)
                projectManager.updateModel(model)
                model.materials.forEach { it.loadTexture(resourceLoader) }
                log(Level.FINE) { "Last project loaded" }
            } catch (e: Exception) {
                log(Level.ERROR) { "Unable to load last project" }
                e.print()
            }
        } else {
            log(Level.FINE) { "No last project found, ignoring" }
        }
    }
}