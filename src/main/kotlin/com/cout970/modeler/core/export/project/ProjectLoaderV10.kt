package com.cout970.modeler.core.export.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.*
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.`object`.GroupTree
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.net.URI
import java.util.*
import java.util.zip.ZipFile

object ProjectLoaderV10 {

    const val VERSION = "1.0"

    val gson = GsonBuilder()
            .setExclusionStrategies(ProjectExclusionStrategy())
            .setPrettyPrinting()
            .registerTypeAdapter(UUID::class.java, UUIDSerializer())
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
            .registerTypeAdapter(IMaterial::class.java, MaterialDeserializer())
            .registerTypeAdapter(IModel::class.java, ModelDeserializer())
            .registerTypeAdapter(IObject::class.java, ObjectDeserializer())
            .registerTypeAdapter(IMesh::class.java, MeshSerializer())
            .registerTypeAdapter(IFaceIndex::class.java, FaceSerializer())
            .registerTypeAdapter(ITransformation::class.java, TransformationSerializer())
            .registerTypeAdapter(IMaterialRef::class.java, MaterialRefSerializer())
            .create()!!


    fun loadProject(zip: ZipFile, path: String): ProgramSave {

        val properties = zip.load<ProjectProperties>("project.json", gson)
                ?: throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson)
                ?: throw IllegalStateException("Missing file 'model.json' inside '$path'")

        checkIntegrity(null, model.objects)
        return ProgramSave(VERSION, properties, model)
    }

    class ModelDeserializer : JsonDeserializer<IModel> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IModel {
            val objs = json.asJsonObject
            val objects = objs.get("objects").asJsonArray
            val materials = objs.get("materials").asJsonArray
            // visibilities is ignored, and set to true

            val materialList = materials.map {
                context.deserialize<IMaterial>(it, IMaterial::class.java)
            }

            val objectsList = objects.map {
                val materialIndex: Int = it.asJsonObject["material"].asJsonObject["materialIndex"].asInt
                val obj = context.deserialize<IObject>(it, IObject::class.java)

                // fix material id adding material refs
                obj.withMaterial(materialList.getOrElse(materialIndex, { MaterialNone }).ref)
            }

            return Model.of(objectsList.associateBy { it.ref }, materialList.associateBy { it.ref })
        }
    }

    class MaterialDeserializer : JsonDeserializer<IMaterial> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMaterial {
            val obj = json.asJsonObject
            return when {
                obj["name"].asString == "noTexture" -> MaterialNone
                else -> TexturedMaterial(
                        name = obj["name"].asString,
                        path = ResourcePath(URI(obj["path"].asString))
                )
            }
        }

    }

    class ObjectDeserializer : JsonDeserializer<IObject> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IObject {
            val obj = json.asJsonObject
            return when (obj["class"].asString) {
                "ObjectCube" -> {
                    ObjectCube(
                            name = context.deserialize(obj["name"], String::class.java),
                            transformation = context.deserialize(obj["transformation"], TRSTransformation::class.java),
                            material = MaterialRefNone,
                            textureOffset = context.deserialize(obj["textureOffset"], IVector2::class.java),
                            textureSize = context.deserialize(obj["textureSize"], IVector2::class.java),
                            mirrored = context.deserialize(obj["mirrored"], Boolean::class.java),
                            visible = true,
                            id = UUID.randomUUID()
                    )
                }
                "Object" -> {
                    Object(
                            name = context.deserialize(obj["name"], String::class.java),
                            mesh = context.deserialize(obj["mesh"], IMesh::class.java),
                            material = MaterialRefNone,
                            visible = true,
                            id = UUID.randomUUID()
                    )
                }
                else -> throw IllegalStateException("Unknown Class: ${obj["class"]}")
            }
        }
    }
}
