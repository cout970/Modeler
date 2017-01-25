package com.cout970.modeler.export

import com.cout970.modeler.model.Material
import com.cout970.modeler.modeleditor.action.ActionImportModel
import com.cout970.modeler.project.Project
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.resource.createPath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
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

    fun importModel(import: ImportProperties) {
        val file = File(import.path)
        when (import.format) {
            ImportFormat.OBJ -> {
                projectManager.modelEditor.historyRecord.doAction(
                        ActionImportModel(projectManager.modelEditor, resourceLoader, import.path) {
                            objImporter.import(file.createPath(), import.flipUV)
                        })
            }
            ImportFormat.TCN -> {
                projectManager.modelEditor.historyRecord.doAction(
                        ActionImportModel(projectManager.modelEditor, resourceLoader, import.path) {
                            tcnImporter.import(file.createPath())
                        })
            }
            ImportFormat.JSON -> {
                projectManager.modelEditor.historyRecord.doAction(
                        ActionImportModel(projectManager.modelEditor, resourceLoader, import.path) {
                            jsonImporter.import(file.createPath())
                        })
            }
        }
    }

    fun exportModel(path: String, format: ExportFormat) {
        val file = File(path)
        when (format) {
            ExportFormat.OBJ -> {
                projectManager.modelEditor.addToQueue {
                    objExporter.export(file.outputStream(), projectManager.modelEditor.model, "materials")
                }
            }
        }
    }
}