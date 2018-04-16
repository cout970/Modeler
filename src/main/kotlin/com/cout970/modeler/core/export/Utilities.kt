package com.cout970.modeler.core.export

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import java.lang.reflect.Modifier
import java.util.zip.ZipFile
import kotlin.math.ln


fun checkIntegrity(parent: Any?, any: Any?, path: String = "") {
    check(any, path)

    val fields = any!!.javaClass.declaredFields
    val validFields = fields.filter {
        !Modifier.isStatic(it.modifiers) &&
                !it.type.isPrimitive
    }

    validFields.forEach {
        it.isAccessible = true
        val value = it.get(any)
        when {
            value == parent -> return@forEach

            it.type.toString().contains("kotlin.Lazy") -> return@forEach
            it.type == List::class.java -> {
                check(value, path)

                val array = value as List<*>
                array.forEachIndexed { index, elem ->

                    checkIntegrity(any, elem, "$path/$index")
                }
            }
            it.type == Map::class.java || it.type == LinkedHashMap::class.java -> {
                check(value, path)

                val map = value as Map<*, *>

                map.forEach { index, elem ->
                    check(index, "$path/index")

                    checkIntegrity(any, elem, "$path/$index")
                }
            }
            !it.type.isArray -> {
                checkIntegrity(any, value, "$path/${it.name}")
            }
        }
    }
}

private fun check(any: Any?, path: String) {
    if (any is LinkedTreeMap<*, *>) {
        throw IllegalStateException("Un-serialized object found after serialization: $path")
    }

    if(any == null) {
        throw IllegalStateException("Null object found after serialize object: $path")
    }
}

inline fun <reified T> ZipFile.load(entryName: String, gson: Gson): T? {
    val entry = getEntry(entryName) ?: return null
    val reader = getInputStream(entry).reader()
    return gson.fromJson(JsonReader(reader), T::class.java)
}