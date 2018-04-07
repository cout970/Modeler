package com.cout970.modeler.core.export.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IGroupTree
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.export.*
import com.cout970.modeler.core.model.`object`.ImmutableBiMultimap
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object ProjectLoaderV11 {

    const val VERSION = "1.1"

    val gson = GsonBuilder()
            .setExclusionStrategies(ProjectExclusionStrategy())
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(UUID::class.java, UUIDSerializer())
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(IMaterial::class.java, MaterialSerializer())
            .registerTypeAdapter(IModel::class.java, ModelSerializer())
            .registerTypeAdapter(IGroupTree::class.java, GroupTreeSerializer())
            .registerTypeAdapter(IGroupRef::class.java, GroupSerializer())
            .registerTypeAdapter(ImmutableBiMultimap::class.java, ImmutableBiMultimapSerializer())
            .registerTypeAdapter(IObject::class.java, ObjectSerializer())
            .registerTypeAdapter(IMesh::class.java, MeshSerializer())
            .registerTypeAdapter(IFaceIndex::class.java, FaceSerializer())
            .registerTypeAdapter(ITransformation::class.java, TransformationSerializer())
            .registerTypeAdapter(IMaterialRef::class.java, MaterialRefSerializer())
            .registerTypeAdapter(IObjectRef::class.java, ObjectRefSerializer())
            .create()!!

    fun loadProject(zip: ZipFile, path: String): ProgramSave {

        val properties = zip.load<ProjectProperties>("project.json", gson)
                         ?: throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson)
                    ?: throw IllegalStateException("Missing file 'model.json' inside '$path'")

        checkIntegrity(null, model.objects)
//        checkIntegrity(null, model.groupTree)
        return ProgramSave(VERSION, properties, model)
    }

    fun saveProject(path: String, save: ProgramSave) {
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
}