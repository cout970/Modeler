package com.cout970.modeler.core.export

import com.cout970.modeler.PathConstants
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
import com.cout970.modeler.core.model.selection.ClipboardNone.Companion.model
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.util.createParentsIfNeeded
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.io.File
import java.lang.reflect.Modifier
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

        if (version != CURRENT_SAVE_VERSION) throw IllegalStateException("Invalid save version")

        val properties = zip.load<ProjectProperties>("project.json", gson) ?:
                         throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson) ?:
                    throw IllegalStateException("Missing file 'model.json' inside '$path'")

        checkIntegrity(null, model.objects)
        return ProgramSave(version, properties, model)
    }

    private fun checkIntegrity(parent: Any?, any: Any?, path: String = "") {
        any ?: throw IllegalStateException("Null object found after serialize object: $path")

        val fields = any.javaClass.declaredFields
        val validFields = fields.filter {
            !Modifier.isStatic(it.modifiers) &&
            !it.type.isPrimitive
        }

        validFields.forEach {
            it.isAccessible = true
            val value = it.get(any)
            when {
                value == parent -> return@forEach

                it.type.toString().contains("kotlin.Lazy") -> return@forEach
                it.type == List::class.java -> {
                    value ?: throw IllegalStateException("Null object found after serialize object: $path")

                    val array = value as List<*>
                    array.forEachIndexed { index, elem ->

                        checkIntegrity(any, elem, "$path/$index")
                    }
                }
                !it.type.isArray -> {
                    checkIntegrity(any, value, "$path/${it.name}")
                }
            }
        }
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
        File(path).createParentsIfNeeded()

        val zip = ZipOutputStream(File(path).outputStream())
        zip.let {
            it.putNextEntry(ZipEntry("version.json"))
            it.write(gson.toJson(save.version).toByteArray())
            it.closeEntry()
            it.putNextEntry(ZipEntry("project.json"))
            it.write(gson.toJson(save.projectProperties).toByteArray())
            it.closeEntry()
            it.putNextEntry(ZipEntry("model.json"))
            it.write(gson.toJson(save.model).toByteArray())
            it.closeEntry()
        }
        zip.close()
    }

    fun loadLastProjectIfExists(projectManager: ProjectManager, gui: Gui) {
        val path = File(PathConstants.LAST_BACKUP_FILE_PATH)
        if (path.exists()) {
            try {
                log(Level.FINE) { "Found last project, loading..." }
                val save = loadProject(path.path)
                projectManager.loadProjectProperties(save.projectProperties)
                projectManager.updateModel(save.model)
                gui.windowHandler.updateTitle(save.projectProperties.name)
                model.materials.forEach { it.loadTexture(resourceLoader) }
                log(Level.FINE) { "Last project loaded" }
                NotificationHandler.push(Notification("Project loaded",
                        "Loaded project from last execution"))
            } catch (e: Exception) {
                log(Level.ERROR) { "Unable to load last project" }
                e.print()
                NotificationHandler.push(Notification("Error loading project",
                        "Unable to load project at '${path.absolutePath}': $e"))
            }
        } else {
            log(Level.FINE) { "No last project found, ignoring" }
        }
    }
}

data class ProgramSave(val version: String, val projectProperties: ProjectProperties, val model: IModel)