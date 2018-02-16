package com.cout970.modeler.core.export

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.lang.reflect.Modifier
import java.util.zip.ZipFile


fun checkIntegrity(parent: Any?, any: Any?, path: String = "") {
    any ?: throw IllegalStateException("Null object found after serialize object: $path")

    val fields = any.javaClass.declaredFields
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
                value ?: throw IllegalStateException("Null object found after serialize object: $path")

                val array = value as List<*>
                array.forEachIndexed { index, elem ->

                    checkIntegrity(any, elem, "$path/$index")
                }
            }
            !it.type.isArray -> {
                checkIntegrity(any, value, "$path/${it.name}")
            }
        }
    }
}

inline fun <reified T> ZipFile.load(entryName: String, gson: Gson): T? {
    val entry = getEntry(entryName) ?: return null
    val reader = getInputStream(entry).reader()
    return gson.fromJson(JsonReader(reader), T::class.java)
}