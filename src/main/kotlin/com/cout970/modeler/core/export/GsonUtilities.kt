package com.cout970.modeler.core.export

import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.to_redo.model.material.IMaterial
import com.cout970.modeler.to_redo.model.material.MaterialNone
import com.cout970.modeler.to_redo.model.material.TexturedMaterial
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import com.google.gson.*
import java.awt.Color
import java.lang.reflect.Type
import java.net.URI

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

class MaterialSerializer : JsonSerializer<IMaterial>, JsonDeserializer<IMaterial> {

    override fun serialize(src: IMaterial, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("name", src.name)
            if (src is TexturedMaterial) {
                addProperty("path", src.path.uri.toString())
            }
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMaterial {
        val obj = json.asJsonObject
        return if (obj["name"].asString == "noTexture") MaterialNone else TexturedMaterial(
                obj["name"].asString, ResourcePath(URI(obj["path"].asString)))
    }
}

class QuadIndicesSerializer : JsonSerializer<McxExporter.QuadStorage.QuadIndices> {

    override fun serialize(src: McxExporter.QuadStorage.QuadIndices, typeOfSrc: Type?,
                           context: JsonSerializationContext?): JsonElement {
        val arr = JsonArray()
        arr.add(JsonArray().apply { add(src.a); add(src.b); add(src.c); add(src.d) })
        arr.add(JsonArray().apply { add(src.at); add(src.bt); add(src.ct); add(src.dt) })
        return arr
    }
}

fun JsonArray.toVector2(): IVector2 = vec2Of(this[0].asNumber, this[1].asNumber)
fun JsonArray.toVector3(): IVector3 = vec3Of(this[0].asNumber, this[1].asNumber, this[2].asNumber)
fun JsonArray.toVector4(): IVector4 = vec4Of(this[0].asNumber, this[1].asNumber, this[2].asNumber, this[3].asNumber)

val JsonElement.asVector2: IVector2 get() = this.asJsonArray.toVector2()
val JsonElement.asVector3: IVector3 get() = this.asJsonArray.toVector3()
val JsonElement.asVector4: IVector4 get() = this.asJsonArray.toVector4()
