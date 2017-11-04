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
import com.cout970.modeler.core.model.selection.ClipboardNone.model
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Created by cout970 on 2017/01/02.
 */
class ExportManager(val resourceLoader: ResourceLoader) {

    companion object {
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

        const val CURRENT_SAVE_VERSION = "1.0"
    }

    fun loadProject(path: String): ProgramSave {
        val zip = ZipFile(path)

        val version = zip.load<String>("version.json", gson) ?:
                      throw IllegalStateException("Missing file 'version.json' inside '$path'")

        if(version != CURRENT_SAVE_VERSION) throw IllegalStateException("Invalid save version")

        val properties = zip.load<ProjectProperties>("project.json", gson) ?:
                         throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson) ?:
                    throw IllegalStateException("Missing file 'model.json' inside '$path'")

        return ProgramSave(version, properties, model)
    }

    inline fun <reified T> ZipFile.load(entryName: String, gson: Gson): T? {
        val entry = getEntry(entryName) ?: return null
        val reader = getInputStream(entry).reader()
        return gson.fromJson(JsonReader(reader), T::class.java)
    }

    fun saveProject(path: String, manger: ProjectManager) {
        saveProject(path, ProgramSave(CURRENT_SAVE_VERSION, manger.projectProperties, manger.model))
    }

    fun saveProject(path: String, save: ProgramSave) {
        File(path).parentFile.let { if (!it.exists()) it.mkdir() }

        val zip = ZipOutputStream(File(path).outputStream())
        zip.let {
            it.putNextEntry(ZipEntry("version.json"))
            it.write(gson.toJson(save.version).toByteArray())
            it.closeEntry()
            it.putNextEntry(ZipEntry("project.json"))
            it.write(gson.toJson(save.projectProperties).toByteArray())
            it.closeEntry()
            it.putNextEntry(ZipEntry("model.json"))
            it.write(gson.toJson(model).toByteArray())
            it.closeEntry()
        }
        zip.close()
    }

    fun loadLastProjectIfExists(projectManager: ProjectManager) {
        val path = File("./saves/last.pff")
        if (path.exists()) {
            try {
                log(Level.FINE) { "Found last project, loading..." }
                val save = loadProject(path.path)
                projectManager.loadProjectProperties(save.projectProperties)
                projectManager.updateModel(save.model)
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

data class ProgramSave(val version: String, val projectProperties: ProjectProperties, val model: IModel)