package com.cout970.modeler.core.config

import com.cout970.modeler.PathConstants
import com.cout970.modeler.core.export.ColorSerializer
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
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

    private val GSON = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(IVector3::class.java, ColorSerializer())
            .setPrettyPrinting()
            .create()

    private val defaultConfig: String

    init {
        defaultConfig = serializeConfig()
    }

    fun loadConfig() {
        val file = File(PathConstants.CONFIG_FILE_PATH)
//        if (file.exists()) {// && !Debugger.STATIC_DEBUG
//            deserializeConfig(file.readText())
//        }
        saveConfig()
    }

    fun saveConfig() {
        val file = File(PathConstants.CONFIG_FILE_PATH).apply { createParentsIfNeeded() }
        file.writeText(serializeConfig())
    }

    fun getDefaultConfig(): JsonObject = JsonParser().parse(defaultConfig).asJsonObject

    fun getConfigAsJson(): JsonObject = JsonParser().parse(serializeConfig()).asJsonObject

    fun setConfigFromJson(cfg: JsonObject) {
        deserializeConfig(GSON.toJson(cfg))
        log(Level.NORMAL) { "Saved Config" }
    }

    private fun deserializeConfig(source: String) {
        val json = JsonParser().parse(source).asJsonObject


        Config::class.java.declaredFields
                .filter { it.name != "INSTANCE" }
                .forEach { field ->
                    field.isAccessible = true
                    val value = json.get(field.name)
                    val jsonValue: Any? = GSON.fromJson(value, field.genericType)
                    if (jsonValue != null) {
                        field.set(null, jsonValue)
                    }
                }
    }

    private fun serializeConfig(): String {
        val clazz = JsonObject()

        Config::class.java.declaredFields
                .filter { it.name != "INSTANCE" }
                .forEach { field ->
                    field.isAccessible = true
                    val value = field.get(null)
                    clazz.add(field.name, GSON.toJsonTree(value))
                }

        return GSON.toJson(clazz)
    }
}