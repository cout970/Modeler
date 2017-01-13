package com.cout970.modeler.export

import com.cout970.modeler.model.Material
import com.cout970.modeler.model.MaterialNone
import com.cout970.modeler.model.TexturedMaterial
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.quatOf
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Created by cout970 on 2017/01/04.
 */

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

class MaterialSerializer : JsonSerializer<Material>, JsonDeserializer<Material> {

    override fun serialize(src: Material, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("name", src.name)
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Material {
        val obj = json.asJsonObject
        return if (obj["name"].asString == "noTexture") MaterialNone else TexturedMaterial(
                obj["name"].asString)
    }
}
