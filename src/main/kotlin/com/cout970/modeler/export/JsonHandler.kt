package com.cout970.modeler.export

import com.google.gson.GsonBuilder

/**
 * Created by cout970 on 2017/01/24.
 */

class JsonImporter {

    val gson = GsonBuilder().setPrettyPrinting().create()!!

    //TODO
//    fun import(path: ResourcePath): Model {
//        val root = JsonParser().parse(path.inputStream().reader()).asJsonObject
//        if (root.has("parent")) {
//            val parent = root["parent"]
//
//        }
//        //ignoring ambientocclusion
//
//        if (root.has("display")) {
//            val display = root["display"].asJsonObject
//        }
//
//        if (root.has("textures")) {
//            val textures = root["textures"].asJsonObject
//        }
//
//        if (root.has("elements")) {
//            for (elem in root["elements"].asJsonArray) {
//                val element = elem.asJsonObject
//
//                val from = element["from"].asJsonArray
//                val to = element["to"].asJsonArray
//                val rotation = element["rotation"].asJsonObject
//                //ignoring shade
//                val faces = element["faces"].asJsonObject
//
//                val func: (JsonObject, Int) -> Unit = { side, dir ->
//                    val uv = side["uv"].asJsonArray
//                    val texture = side["texture"].asString
//                    //ignoring cullface
//                    val faceRotation = side["rotation"].asInt
//                    //ignoring tintindex
//
//                }
//
//                if(faces.has("down")) func(faces["down"].asJsonObject, 0)
//                if(faces.has("up")) func(faces["up"].asJsonObject, 1)
//                if(faces.has("north")) func(faces["north"].asJsonObject, 2)
//                if(faces.has("south")) func(faces["south"].asJsonObject, 3)
//                if(faces.has("west")) func(faces["west"].asJsonObject, 4)
//                if(faces.has("east")) func(faces["east"].asJsonObject, 5)
//
//            }
//        } else if (!root.has("parent")) {
//            throw IllegalStateException("EmptyModel: $path")
//        }
//        return Model(listOf())
//    }
}