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
import com.cout970.modeler.core.model.`object`.BiMultimap
import com.cout970.modeler.core.model.`object`.GroupTree
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.*
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import java.io.File
import java.lang.reflect.Type
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object ProjectLoaderV12 {

    const val VERSION = "1.2"

    val gson = GsonBuilder()
            .setExclusionStrategies(ProjectExclusionStrategy())
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(UUID::class.java, UUIDSerializer())
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(IMaterial::class.java, MaterialSerializer())
            .registerTypeAdapter(IModel::class.java, serializerOf<Model>())
            .registerTypeAdapter(IGroupTree::class.java, GroupTreeSerializer())
            .registerTypeAdapter(IGroupRef::class.java, GroupRefSerializer())
            .registerTypeAdapter(IGroup::class.java, serializerOf<Group>())
            .registerTypeAdapter(BiMultimap::class.java, BiMultimapSerializer())
            .registerTypeAdapter(ImmutableMap::class.java, ImmutableMapSerializer())
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

        checkIntegrity(null, listOf(model.objectMap, model.materialMap, model.groupMap, model.groupTree, model.groupObjects))
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

    class GroupTreeSerializer : JsonSerializer<IGroupTree>, JsonDeserializer<IGroupTree> {

        data class Aux(val key: IGroupRef, val value: Set<IGroupRef>)
        data class Aux2(val key: IGroupRef, val value: IGroupRef)

        override fun serialize(src: IGroupTree, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val tree = src as GroupTree
            return JsonObject().apply {
                add("childMap", JsonArray().also { array ->
                    tree.childMap.map { Aux(it.key, it.value) }.map { context.serialize(it) }.forEach { it -> array.add(it) }
                })
                add("parentMap", JsonArray().also { array ->
                    tree.parentMap.map { Aux2(it.key, it.value) }.map { context.serialize(it) }.forEach { it -> array.add(it) }
                })
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IGroupTree {
            if (json.isJsonNull) return GroupTree.emptyTree()
            val obj = json.asJsonObject

            val childMapArray = obj["childMap"].asJsonArray
            val parentMapArray = obj["parentMap"].asJsonArray

            val childMap = childMapArray
                    .map { context.deserialize(it, Aux::class.java) as Aux }
                    .map { it.key to it.value }
                    .toMap()
                    .toImmutableMap()

            val parentMap = parentMapArray
                    .map { context.deserialize(it, Aux2::class.java) as Aux2 }
                    .map { it.key to it.value }
                    .toMap()
                    .toImmutableMap()

            return GroupTree(parentMap, childMap)
        }
    }
}