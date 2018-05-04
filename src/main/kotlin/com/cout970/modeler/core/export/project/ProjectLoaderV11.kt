package com.cout970.modeler.core.export.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.export.*
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.`object`.ImmutableBiMultimap
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
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
            .registerTypeAdapter(IGroupRef::class.java, serializerOf<GroupRef>())
            .registerTypeAdapter(IGroup::class.java, serializerOf<Group>())
            .registerTypeAdapter(ImmutableBiMultimap::class.java, BiMultimapSerializer())
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

        checkIntegrity(null, listOf(model.objectMap, model.materialMap, model.groupMap, model.groupTree))
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

    class ModelSerializer : JsonSerializer<IModel>, JsonDeserializer<IModel> {

        override fun serialize(src: IModel, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IModel {
            val model: Model = context.deserialize(json, Model::class.java)

            return Model.of(model.objectMap, model.materialMap)
        }
    }
}