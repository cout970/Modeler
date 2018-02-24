package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import com.google.gson.*
import java.awt.Color
import java.lang.reflect.Type
import java.net.URI
import java.util.*

/**
 * Created by cout970 on 2017/01/04.
 */

class ProjectExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.declaredClass == kotlin.Lazy::class.java
    }
}

class Vector3Serializer : JsonSerializer<IVector3>, JsonDeserializer<IVector3> {

    override fun serialize(src: IVector3, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().apply {
            add(src.x)
            add(src.y)
            add(src.z)
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IVector3 {
        val array = json.asJsonArray
        return vec3Of(array[0].asNumber, array[1].asNumber, array[2].asNumber)
    }
}

class ColorSerializer : JsonSerializer<IVector3>, JsonDeserializer<IVector3> {

    override fun serialize(src: IVector3, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val str = Integer.toHexString(Color(src.xf, src.yf, src.zf, 1f).rgb).run {
            substring(2, length)
        }
        return JsonPrimitive(str)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IVector3 {
        val str = json.asString
        val color = Color(str.toInt(16))
        return vec3Of(color.red, color.green, color.blue) / 255f
    }
}

class Vector2Serializer : JsonSerializer<IVector2>, JsonDeserializer<IVector2> {

    override fun serialize(src: IVector2, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().apply {
            add(src.x)
            add(src.y)
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IVector2 {
        val array = json.asJsonArray
        return vec2Of(array[0].asNumber, array[1].asNumber)
    }
}

class QuaternionSerializer : JsonSerializer<IQuaternion>, JsonDeserializer<IQuaternion> {

    override fun serialize(src: IQuaternion, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().apply {
            add(src.x)
            add(src.y)
            add(src.z)
            add(src.w)
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IQuaternion {
        val array = json.asJsonArray
        return quatOf(array[0].asNumber, array[1].asNumber, array[2].asNumber, array[3].asNumber)
    }
}

class ModelSerializer : JsonSerializer<IModel>, JsonDeserializer<IModel> {

    override fun serialize(src: IModel, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IModel {
        return context.deserialize(json, Model::class.java)
    }
}

class MeshSerializer : JsonSerializer<IMesh>, JsonDeserializer<IMesh> {

    override fun serialize(src: IMesh, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMesh {
        return context.deserialize(json, Mesh::class.java)
    }
}

class FaceSerializer : JsonSerializer<IFaceIndex>, JsonDeserializer<IFaceIndex> {

    override fun serialize(src: IFaceIndex, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IFaceIndex {
        return context.deserialize(json, FaceIndex::class.java)
    }
}

class TransformationSerializer : JsonSerializer<ITransformation>, JsonDeserializer<ITransformation> {

    override fun serialize(src: ITransformation, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ITransformation {
        return context.deserialize(json, TRSTransformation::class.java)
    }
}

class ObjectSerializer : JsonSerializer<IObject>, JsonDeserializer<IObject> {

    override fun serialize(src: IObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src).asJsonObject.apply {
            addProperty("class", src.javaClass.simpleName)
        }
    }

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

class ObjectRefSerializer : JsonSerializer<IObjectRef>, JsonDeserializer<IObjectRef> {

    override fun serialize(src: IObjectRef, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src.objectId)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IObjectRef {
        return ObjectRef(context.deserialize(json, UUID::class.java))
    }
}

class MaterialSerializer : JsonSerializer<IMaterial>, JsonDeserializer<IMaterial> {

    override fun serialize(src: IMaterial, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("name", src.name)
            if (src is TexturedMaterial) {
                addProperty("path", src.path.uri.toString())
                add("id", context.serialize(src.id))
            }
        }
    }

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

class MaterialRefSerializer : JsonSerializer<IMaterialRef>, JsonDeserializer<IMaterialRef> {

    override fun serialize(src: IMaterialRef, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.materialId.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMaterialRef {
        val uuid = UUID.fromString(json.asString)
        if (uuid == MaterialRefNone.materialId) return MaterialRefNone
        return MaterialRef(uuid)
    }
}

class UUIDSerializer : JsonSerializer<UUID>, JsonDeserializer<UUID> {

    override fun serialize(src: UUID, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UUID {
        return UUID.fromString(json.asString)
    }
}

class QuadIndicesSerializer : JsonSerializer<QuadIndices>, JsonDeserializer<QuadIndices> {

    override fun serialize(src: QuadIndices, typeOfSrc: Type?,
                           context: JsonSerializationContext?): JsonElement {
        val arr = JsonArray()
        arr.add(JsonArray().apply { add(src.a); add(src.b); add(src.c); add(src.d) })
        arr.add(JsonArray().apply { add(src.at); add(src.bt); add(src.ct); add(src.dt) })
        return arr
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): QuadIndices {
        val arr = json.asJsonArray
        val pos = arr[0].asJsonArray
        val tex = arr[1].asJsonArray
        return QuadIndices(
                pos[0].asInt, pos[1].asInt, pos[2].asInt, pos[3].asInt,
                tex[0].asInt, tex[1].asInt, tex[2].asInt, tex[3].asInt
        )
    }
}

fun JsonArray.toVector2(): IVector2 = vec2Of(this[0].asNumber, this[1].asNumber)
fun JsonArray.toVector3(): IVector3 = vec3Of(this[0].asNumber, this[1].asNumber, this[2].asNumber)
fun JsonArray.toVector4(): IVector4 = vec4Of(this[0].asNumber, this[1].asNumber, this[2].asNumber, this[3].asNumber)

val JsonElement.asVector2: IVector2 get() = this.asJsonArray.toVector2()
val JsonElement.asVector3: IVector3 get() = this.asJsonArray.toVector3()
val JsonElement.asVector4: IVector4 get() = this.asJsonArray.toVector4()
