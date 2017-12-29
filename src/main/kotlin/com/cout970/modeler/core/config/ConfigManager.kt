package com.cout970.modeler.core.config

import com.cout970.modeler.Debugger
import com.cout970.modeler.PathConstants
import com.cout970.modeler.core.export.ColorSerializer
import com.cout970.modeler.util.createParentsIfNeeded
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

/**
 * Created by cout970 on 2016/12/28.
 */
object ConfigManager {

    fun loadConfig() {
        val file = File(PathConstants.CONFIG_FILE_PATH)
        if (file.exists() && !Debugger.DEBUG) {
            val gson = GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(IVector3::class.java, ColorSerializer())
                    .setPrettyPrinting()
                    .create()

            val json = JsonParser().parse(file.reader()).asJsonObject

            Config::class.java.declaredFields
                    .filter { it.name != "INSTANCE" }
                    .forEach { field ->
                        field.isAccessible = true
                        val value = json.get(field.name)
                        val jsonValue: Any? = gson.fromJson(value, field.genericType)
                        if (jsonValue != null) {
                            field.set(null, jsonValue)
                        }
                    }
        }
        saveConfig()
    }

    fun saveConfig() {
        val file = File(PathConstants.CONFIG_FILE_PATH).apply { createParentsIfNeeded() }
        val clazz = JsonObject()
        val gson = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(IVector3::class.java, ColorSerializer())
                .setPrettyPrinting()
                .create()

        Config::class.java.declaredFields
                .filter { it.name != "INSTANCE" }
                .forEach { field ->
                    field.isAccessible = true
                    val value = field.get(null)
                    clazz.add(field.name, gson.toJsonTree(value))
                }

        file.writeText(gson.toJson(clazz))
    }
}