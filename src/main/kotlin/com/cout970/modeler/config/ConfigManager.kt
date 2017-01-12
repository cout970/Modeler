package com.cout970.modeler.config

import com.cout970.modeler.log.Logger
import com.cout970.modeler.util.createIfNeeded
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

/**
 * Created by cout970 on 2016/12/28.
 */
object ConfigManager {

    fun loadConfig() {
        val file = File("config.json")
        if (file.exists() && !Logger.DEBUG) {
            val gson = GsonBuilder().setLenient().setPrettyPrinting().create()
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
        val file = File("config.json")
        val gson = GsonBuilder().setLenient().setPrettyPrinting().create()
        val clazz = JsonObject()

        Config::class.java.declaredFields.filter { it.name != "INSTANCE" }.forEach { field ->
            field.isAccessible = true
            val value = field.get(null)
            clazz.add(field.name, gson.toJsonTree(value))
        }
        file.writeText(gson.toJson(clazz))
    }
}