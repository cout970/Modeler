package com.cout970.modeler.core.export

import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Created by cout970 on 2017/01/24.
 */

class JsonImporter {

    val gson = GsonBuilder().setPrettyPrinting().create()!!

    //TODO
//    fun import(path: ResourcePath): Model {
//        val model = parse(path)
//
//        val materials = mutableMapOf<String, TexturedMaterial>()
//        for ((name, subPath) in model.textures) {
//            materials += name to TexturedMaterial(name, path.resolve(subPath + ".png"))
//        }
//
//        val quads = mutableMapOf<String, MutableList<Quad>>()
//        for (element in model.elements) {
//            element.sides.forEach { side, face ->
//                val list = quads[face.texture] ?: mutableListOf<Quad>().apply { quads.put(face.texture, this) }
//                list += getQuad(element, side, face)
//            }
//        }
//
//        val groups = quads.map { (name, list) ->
//            Meshes.quadsToMesh(list)
//        }
//
//        return Model(groups, ModelResources(materials.values.toList()))
//    }
//
//    private fun getQuad(element: Element, side: Side, face: Face): Quad {
//        val n = vec3Of(
//                Math.min(element.from.xf, element.to.xf),
//                Math.min(element.from.yf, element.to.yf),
//                Math.min(element.from.zf, element.to.zf)
//        )
//        val p = vec3Of(
//                Math.max(element.from.xf, element.to.xf),
//                Math.max(element.from.yf, element.to.yf),
//                Math.max(element.from.zf, element.to.zf)
//        )
//
//        return when (side) {
//            JsonImporter.Side.DOWN -> {
//                Quad.create(
//                        vec3Of(p.x, n.y, n.z),
//                        vec3Of(p.x, n.y, p.z),
//                        vec3Of(n.x, n.y, p.z),
//                        vec3Of(n.x, n.y, n.z)
//                ).setTexture1(face)
//            }
//            JsonImporter.Side.UP -> {
//                Quad.create(
//                        vec3Of(n.x, p.y, p.z),
//                        vec3Of(p.x, p.y, p.z),
//                        vec3Of(p.x, p.y, n.z),
//                        vec3Of(n.x, p.y, n.z)
//                ).setTexture(face)
//            }
//            JsonImporter.Side.NORTH -> {
//                Quad.create(
//                        vec3Of(n.x, p.y, n.z),
//                        vec3Of(p.x, p.y, n.z),
//                        vec3Of(p.x, n.y, n.z),
//                        vec3Of(n.x, n.y, n.z)
//                ).setTexture(face)
//            }
//            JsonImporter.Side.SOUTH -> {
//                Quad.create(
//                        vec3Of(p.x, n.y, p.z),
//                        vec3Of(p.x, p.y, p.z),
//                        vec3Of(n.x, p.y, p.z),
//                        vec3Of(n.x, n.y, p.z)
//                ).setTexture1(face)
//            }
//            JsonImporter.Side.WEST -> {
//                Quad.create(
//                        vec3Of(n.x, n.y, p.z),
//                        vec3Of(n.x, p.y, p.z),
//                        vec3Of(n.x, p.y, n.z),
//                        vec3Of(n.x, n.y, n.z)
//                ).setTexture1(face)
//            }
//            JsonImporter.Side.EAST -> {
//                Quad.create(
//                        vec3Of(p.x, p.y, n.z),
//                        vec3Of(p.x, p.y, p.z),
//                        vec3Of(p.x, n.y, p.z),
//                        vec3Of(p.x, n.y, n.z)
//                ).setTexture(face)
//            }
//        }
//    }
//
//    fun Quad.setTexture(face: Face): Quad {
//        val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
//        val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
//        return Quad(
//                a.copy(tex = vec2Of(uv1.x, uv0.y)),
//                b.copy(tex = vec2Of(uv0.x, uv0.y)),
//                c.copy(tex = vec2Of(uv0.x, uv1.y)),
//                d.copy(tex = vec2Of(uv1.x, uv1.y))
//        )
//    }
//
//    fun Quad.setTexture1(face: Face): Quad {
//        val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
//        val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
//        return Quad(
//                a.copy(tex = vec2Of(uv1.x, uv1.y)),
//                b.copy(tex = vec2Of(uv1.x, uv0.y)),
//                c.copy(tex = vec2Of(uv0.x, uv0.y)),
//                d.copy(tex = vec2Of(uv0.x, uv1.y))
//        )
//    }

    fun parse(path: ResourcePath): JsonModel {
        val model = JsonModel()
        val root = JsonParser().parse(path.inputStream().reader()).asJsonObject
        if (root.has("parent")) {
            val parent = root["parent"].asString
            val parentModel = parse(path.parent!!.resolve(parent))
            model.elements += parentModel.elements
            model.textures += parentModel.textures
            model.display.states += parentModel.display.states
        }
        //ignoring ambientocclusion

        if (root.has("display")) {
            val display = root["display"].asJsonObject
            for ((key, value) in display.entrySet()) {
                val obj = value.asJsonObject
                val translation = obj["translation"]?.asVector3 ?: Vector3.ORIGIN
                val rotation = obj["rotation"]?.asVector3 ?: Vector3.ORIGIN
                val scale = obj["scale"]?.asVector3 ?: Vector3.ORIGIN

                model.display.states += key to Transform(translation, rotation, scale)
            }
        }

        if (root.has("textures")) {
            val textures = root["textures"].asJsonObject
            for ((key, value) in textures.entrySet()) {
                model.textures += key to value.asString
            }
        }

        if (root.has("elements")) {
            for (elem in root["elements"].asJsonArray) {
                val element = elem.asJsonObject

                val from = element["from"].asJsonArray
                val to = element["to"].asJsonArray
                var rotation: ElementRotation? = null
                //ignoring shade
                val faces = element["faces"].asJsonObject
                val sides = mutableMapOf<Side, Face>()

                if (element.has("rotation")) {
                    val rot = element["rotation"].asJsonObject
                    val axis = when (rot["axis"].asString) {
                        "x" -> vec3Of(1, 0, 0)
                        "y" -> vec3Of(1, 0, 0)
                        "z" -> vec3Of(1, 0, 0)
                        else -> throw IllegalStateException(
                                "Invalid rotation axis (${rot["axis"].asString}) in file: $path")
                    }
                    rotation = ElementRotation(rot["origin"].asVector3, axis, rot["angle"].asFloat,
                            rot["rescale"]?.asBoolean ?: false)
                }

                val func: (JsonObject, Side) -> Unit = { side, dir ->
                    val uv = side["uv"].asJsonArray
                    val texture = side["texture"].asString.substring(1)
                    //ignoring cullface
                    val faceRotation = side["rotation"]?.asFloat ?: 0f
                    //ignoring tintindex
                    sides += dir to Face(uv.toVector4(), texture, faceRotation)
                }

                if (faces.has("down")) func(faces["down"].asJsonObject, Side.DOWN)
                if (faces.has("up")) func(faces["up"].asJsonObject, Side.UP)
                if (faces.has("north")) func(faces["north"].asJsonObject, Side.NORTH)
                if (faces.has("south")) func(faces["south"].asJsonObject, Side.SOUTH)
                if (faces.has("west")) func(faces["west"].asJsonObject, Side.WEST)
                if (faces.has("east")) func(faces["east"].asJsonObject, Side.EAST)

                model.elements += Element(from.toVector3(), to.toVector3(), rotation, sides)
            }
        } else if (!root.has("parent")) {
            throw IllegalStateException("Empty model file (no parent or elements) at $path")
        }
        return model
    }

    class JsonModel(
            val textures: MutableMap<String, String> = mutableMapOf(),
            var display: Display = Display(mutableMapOf()),
            val elements: MutableList<Element> = mutableListOf())

    class Display(val states: MutableMap<String, Transform>)

    class Element(val from: IVector3, val to: IVector3, val rotation: ElementRotation?, val sides: Map<Side, Face>)

    class Face(val uv: IVector4, val texture: String, val rotation: Float)

    enum class Side { DOWN, UP, NORTH, SOUTH, WEST, EAST }

    class ElementRotation(val origin: IVector3, val axis: IVector3, val angle: Float, val rescale: Boolean)

    class Transform(val translation: IVector3, val rotation: IVector3, val scale: IVector3)
}