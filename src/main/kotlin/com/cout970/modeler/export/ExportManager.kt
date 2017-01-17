package com.cout970.modeler.export

import com.cout970.modeler.ResourceManager
import com.cout970.modeler.model.Material
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.action.ActionImportModel
import com.cout970.modeler.project.Project
import com.cout970.modeler.view.popup.Missing
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
class ExportManager(val modelController: ModelController, val resourceManager: ResourceManager) {

    val objImporter = ObjImporter()
    val objExporter = ObjExporter()
    val tcnImporter = TcnImporter()

    val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(Material::class.java, MaterialSerializer())
            .create()!!

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
        zip.use {
            it.putNextEntry(ZipEntry("project.json"))
            it.write(json.toByteArray())
            it.closeEntry()
        }
    }

    fun importModel(path: String, format: ImportFormat) {
        val file = File(path)
        when (format) {
            ImportFormat.OBJ -> {
                modelController.historyRecord.doAction(ActionImportModel(modelController, resourceManager, path) {
                    objImporter.import(file.toPath(), true)
                })
            }
            ImportFormat.TCN -> {
                modelController.historyRecord.doAction(ActionImportModel(modelController, resourceManager, path) {
                    tcnImporter.import(file.inputStream())
                })
            }
            ImportFormat.JSON -> Missing("Not implemented Json model import")
        }
    }

    fun exportModel(path: String, format: ExportFormat) {
        val file = File(path)
        when (format) {
            ExportFormat.OBJ -> {
                modelController.addToQueue {
                    objExporter.export(file.outputStream(), modelController.model, "materials")
                }
            }
        }
    }
}