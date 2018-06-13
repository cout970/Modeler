package com.cout970.modeler.core.export

import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import java.lang.reflect.Modifier
import java.util.zip.ZipFile


fun checkIntegrity(vararg things: Any?) {
    things.forEachIndexed { index, any ->
        checkIntegrity2(null, "", { any }, "root/$index")
    }
}

fun checkIntegrity2(parent: Any?, name: String, getter: () -> Any?, path: String = "") {
    val value = getter()
    check(value, "$path/$name")

    when (value) {
        null, parent, is kotlin.Lazy<*> -> return

        is List<*> -> {
            value.forEachIndexed { index, elem ->
                checkIntegrity2(value, index.toString(), { elem }, "$path/$index")
            }
        }

        is Array<*> -> {
            value.forEachIndexed { index, elem ->
                checkIntegrity2(value, index.toString(), { elem }, "$path/$index")
            }
        }

        is Map<*, *> -> {
            value.toList().forEachIndexed { index, (k, v) ->
                checkIntegrity2(value, index.toString(), { k }, "$path/$index/key")
                checkIntegrity2(value, index.toString(), { v }, "$path/$index/value")
            }
        }

        is TexturedMaterial -> {
            check(value.name, "$path/name")
            check(value.path, "$path/path")
            check(value.id, "$path/id")
        }

        is ColoredMaterial -> {
            check(value.name, "$path/name")
            check(value.color, "$path/color")
            check(value.id, "$path/id")
        }

        else -> checkIntegrityAux(value, "$path/$name")
    }
}

fun checkIntegrityAux(any: Any?, path: String = "") {
    any!!.javaClass
            .declaredFields
            .filter { !Modifier.isStatic(it.modifiers) && !it.type.isPrimitive && !it.isSynthetic }
            .onEach { it.isAccessible = true }
            .forEach { checkIntegrity2(any, it.name, { it.get(any) }, path) }
}

private fun check(any: Any?, path: String) {
    if (any is LinkedTreeMap<*, *>) {
        throw IllegalStateException("Un-serialized object found after serialization: $path")
    }

    if (any == null) {
        throw IllegalStateException("Null object found after serialize object: $path")
    }
}

inline fun <reified T> ZipFile.load(entryName: String, gson: Gson): T? {
    val entry = getEntry(entryName) ?: return null
    val reader = getInputStream(entry).reader()
    return gson.fromJson(JsonReader(reader), T::class.java)
}