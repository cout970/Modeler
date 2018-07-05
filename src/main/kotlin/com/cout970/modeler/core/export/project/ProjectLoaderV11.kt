package com.cout970.modeler.core.export.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.MutableGroupTree
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.animation.animationOf
import com.cout970.modeler.core.export.*
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.`object`.*
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.toImmutable
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.collections.immutable.ImmutableMap
import java.lang.reflect.Type
import java.net.URI
import java.util.*
import java.util.zip.ZipFile

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
            .registerTypeAdapter(IGroupRef::class.java, GroupRefSerializer())
            .registerTypeAdapter(IMaterialRef::class.java, MaterialRefSerializer())
            .registerTypeAdapter(IObjectRef::class.java, ObjectRefSerializer())
            .registerTypeAdapter(ITransformation::class.java, TransformationSerializer())
            .registerTypeAdapter(BiMultimap::class.java, BiMultimapSerializer())
            .registerTypeAdapter(ImmutableMap::class.java, ImmutableMapSerializer())
            .registerTypeAdapter(IModel::class.java, ModelSerializer())
            .registerTypeAdapter(IMaterial::class.java, MaterialSerializer())
            .registerTypeAdapter(IObject::class.java, ObjectSerializer())
            .registerTypeAdapter(IMesh::class.java, MeshSerializer())
            .registerTypeAdapter(IFaceIndex::class.java, FaceSerializer())
            .create()!!


    fun loadProject(zip: ZipFile, path: String): ProgramSave {

        val properties = zip.load<ProjectProperties>("project.json", gson)
                ?: throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson)
                ?: throw IllegalStateException("Missing file 'model.json' inside '$path'")

        checkIntegrity(listOf(model.objectMap, model.materialMap, model.groupMap, model.tree))
        return ProgramSave(VERSION, properties, model, animationOf(), emptyList())
    }

    class ModelSerializer : JsonDeserializer<IModel> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IModel {
            val obj = json.asJsonObject
            val objectMap = context.deserializeT<Map<IObjectRef, IObject>>(obj["objectMap"])
            val materialMap = context.deserializeT<Map<IMaterialRef, IMaterial>>(obj["materialMap"])

            return Model.of(
                    objectMap = objectMap,
                    materialMap = materialMap,
                    groupTree = MutableGroupTree(RootGroupRef, objectMap.keys.toMutableList()).toImmutable()
            )
        }
    }

    class MaterialSerializer : JsonDeserializer<IMaterial> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMaterial {
            val obj = json.asJsonObject
            return when {
                obj["name"].asString == "noTexture" -> MaterialNone
                else -> {
                    val id = context.deserialize<UUID>(obj["id"], UUID::class.java)
                    TexturedMaterial(obj["name"].asString, ResourcePath(URI(obj["path"].asString)), id)
                }
            }
        }
    }

    class ObjectSerializer : JsonDeserializer<IObject> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IObject {
            val obj = json.asJsonObject
            return when (obj["class"].asString) {
                "ObjectCube" -> {
                    ObjectCube(
                            name = context.deserialize(obj["name"], String::class.java),
                            transformation = context.deserialize(obj["transformation"], TRSTransformation::class.java),
                            material = context.deserialize(obj["material"], IMaterialRef::class.java),
                            textureOffset = context.deserialize(obj["textureOffset"], IVector2::class.java),
                            textureSize = context.deserialize(obj["textureSize"], IVector2::class.java),
                            mirrored = context.deserialize(obj["mirrored"], Boolean::class.java),
                            visible = context.deserialize(obj["visible"], Boolean::class.java),
                            id = context.deserialize(obj["id"], UUID::class.java)
                    )
                }
                "Object" -> Object(
                        name = context.deserialize(obj["name"], String::class.java),
                        mesh = context.deserialize(obj["mesh"], IMesh::class.java),
                        material = context.deserialize(obj["material"], IMaterialRef::class.java),
                        visible = context.deserialize(obj["visible"], Boolean::class.java),
                        id = context.deserialize(obj["id"], UUID::class.java)
                )


                else -> throw IllegalStateException("Unknown Class: ${obj["class"]}")
            }
        }
    }

    class MeshSerializer : JsonDeserializer<IMesh> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMesh {
            val obj = json.asJsonObject

            return Mesh(
                    pos = context.deserializeT(obj["pos"]),
                    tex = context.deserializeT(obj["tex"]),
                    faces = context.deserializeT(obj["faces"])
            )
        }
    }

    class FaceSerializer : JsonDeserializer<IFaceIndex> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IFaceIndex {
            val obj = json.asJsonObject

            return FaceIndex(
                    pos = context.deserializeT(obj["pos"]),
                    tex = context.deserializeT(obj["tex"])
            )
        }
    }

    class BiMultimapSerializer : JsonDeserializer<BiMultimap<IGroupRef, IObjectRef>> {

        data class Aux(val key: IGroupRef, val value: List<IObjectRef>)

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BiMultimap<IGroupRef, IObjectRef> {
            if (json.isJsonNull || (json.isJsonArray && json.asJsonArray.size() == 0))
                return emptyBiMultimap()

            val array = json.asJsonArray
            val list = array.map { context.deserialize(it, Aux::class.java) as Aux }

            return biMultimapOf(*list.map { it.key to it.value }.toTypedArray())
        }
    }
}