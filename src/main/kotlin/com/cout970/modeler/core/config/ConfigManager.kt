package com.cout970.modeler.core.config

import com.cout970.modeler.Debugger
import com.cout970.modeler.core.export.ColorSerializer
import com.cout970.modeler.core.resource.createIfNeeded
import com.cout970.vector.api.IVector3
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

/**
 * Created by cout970 on 2016/12/28.
 */
object ConfigManager {

    val configPath = "data/config.json"

    fun loadConfig() {
        val file = File(configPath)
        if (file.exists() && !Debugger.DEBUG) {
            val gson = GsonBuilder().setLenient().registerTypeAdapter(IVector3::class.java,
                    ColorSerializer()).setPrettyPrinting().create()
            val json = JsonParser().parse(file.createIfNeeded().reader()).asJsonObject

            Config::class.java.declaredFields.filter { it.name != "INSTANCE" }.forEach { field ->
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
        File("data").let { if(!it.exists()) it.mkdir() }
        val file = File(configPath)
        val gson = GsonBuilder().setLenient().registerTypeAdapter(IVector3::class.java,
                ColorSerializer()).setPrettyPrinting().create()
        val clazz = JsonObject()

        Config::class.java.declaredFields.filter { it.name != "INSTANCE" }.forEach { field ->
            field.isAccessible = true
            val value = field.get(null)
            clazz.add(field.name, gson.toJsonTree(value))
        }
        file.writeText(gson.toJson(clazz))
    }
}