package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Created by cout970 on 2017/01/24.
 */

class JsonImporter {

    val gson = GsonBuilder().setPrettyPrinting().create()!!


    fun import(path: ResourcePath): IModel {
        val model = parse(path)

        val materialList = mutableListOf<IMaterial>()
        val materials = mutableMapOf<String, IMaterialRef>()
        for ((name, subPath) in model.textures) {
            val index = materialList.size
            materialList += TexturedMaterial(name, path.resolve(subPath + ".png"))
            materials += name to MaterialRef(index)
        }

        val obj = mutableListOf<IObject>()
        for (element in model.elements) {
            var isCube = false
            if (element.sides.size == 6) {
                val texture = element.sides[Side.NORTH]?.texture
                if (element.sides.all { it.value.texture == texture }) {
                    val cube = ObjectCube(
                            name = "Shape_${obj.size}",
                            pos = element.from,
                            size = element.to - element.from,
                            rotation = Quaternion.IDENTITY,
                            material = materials[texture]!!,
                            rotationPivot = Vector3.ORIGIN,
                            textureSize = vec2Of(16),
                            textureOffset = vec2Of(0),
                            mirrored = false,
                            transformation = TRSTransformation.IDENTITY
                    )
                    obj += cube
                    isCube = true
                }
            }
            if (!isCube) {
                //TODO fixme
                val groups = element.sides.entries.groupBy { it.value.texture }

                groups.forEach { texture, sides ->
                    val mesh = sides
                            .map { getSide(element, it.key, it.value) }
                            .reduce { acum, mesh -> acum.merge(mesh) }

                    val shape = Object(
                            name = "Shape_${obj.size}",
                            mesh = mesh,
                            transformation = TRSTransformation.IDENTITY,
                            material = materials[texture]!!
                    )
                    obj += shape
                }
            }
        }

        return Model.of(obj, materialList)
    }

    private fun getSide(element: Element, side: Side, face: Face): IMesh {
        val n = vec3Of(
                Math.min(element.from.xf, element.to.xf),
                Math.min(element.from.yf, element.to.yf),
                Math.min(element.from.zf, element.to.zf)
        )
        val p = vec3Of(
                Math.max(element.from.xf, element.to.xf),
                Math.max(element.from.yf, element.to.yf),
                Math.max(element.from.zf, element.to.zf)
        )

        return when (side) {
            JsonImporter.Side.DOWN -> {
                val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
                val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
                Mesh(
                        pos = listOf(
                                vec3Of(p.x, n.y, n.z),
                                vec3Of(p.x, n.y, p.z),
                                vec3Of(n.x, n.y, p.z),
                                vec3Of(n.x, n.y, n.z)),
                        tex = listOf(
                                vec2Of(uv1.x, uv1.y),
                                vec2Of(uv1.x, uv0.y),
                                vec2Of(uv0.x, uv0.y),
                                vec2Of(uv0.x, uv1.y)
                        ),
                        faces = listOf(FaceIndex(listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
                )
            }
            JsonImporter.Side.UP -> {
                val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
                val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
                Mesh(
                        pos = listOf(
                                vec3Of(n.x, p.y, p.z),
                                vec3Of(p.x, p.y, p.z),
                                vec3Of(p.x, p.y, n.z),
                                vec3Of(n.x, p.y, n.z)),
                        tex = listOf(
                                vec2Of(uv1.x, uv0.y),
                                vec2Of(uv0.x, uv0.y),
                                vec2Of(uv0.x, uv1.y),
                                vec2Of(uv1.x, uv1.y)
                        ),
                        faces = listOf(FaceIndex(listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
                )
            }
            JsonImporter.Side.NORTH -> {
                val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
                val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
                Mesh(
                        pos = listOf(
                                vec3Of(n.x, p.y, n.z),
                                vec3Of(p.x, p.y, n.z),
                                vec3Of(p.x, n.y, n.z),
                                vec3Of(n.x, n.y, n.z)),
                        tex = listOf(
                                vec2Of(uv1.x, uv0.y),
                                vec2Of(uv0.x, uv0.y),
                                vec2Of(uv0.x, uv1.y),
                                vec2Of(uv1.x, uv1.y)
                        ),
                        faces = listOf(FaceIndex(listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
                )
            }
            JsonImporter.Side.SOUTH -> {
                val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
                val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
                Mesh(
                        pos = listOf(
                                vec3Of(p.x, n.y, p.z),
                                vec3Of(p.x, p.y, p.z),
                                vec3Of(n.x, p.y, p.z),
                                vec3Of(n.x, n.y, p.z)),
                        tex = listOf(
                                vec2Of(uv1.x, uv1.y),
                                vec2Of(uv1.x, uv0.y),
                                vec2Of(uv0.x, uv0.y),
                                vec2Of(uv0.x, uv1.y)
                        ),
                        faces = listOf(FaceIndex(listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
                )
            }
            JsonImporter.Side.WEST -> {
                val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
                val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
                Mesh(
                        pos = listOf(
                                vec3Of(n.x, n.y, p.z),
                                vec3Of(n.x, p.y, p.z),
                                vec3Of(n.x, p.y, n.z),
                                vec3Of(n.x, n.y, n.z)),
                        tex = listOf(
                                vec2Of(uv1.x, uv1.y),
                                vec2Of(uv1.x, uv0.y),
                                vec2Of(uv0.x, uv0.y),
                                vec2Of(uv0.x, uv1.y)
                        ),
                        faces = listOf(FaceIndex(listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
                )
            }
            JsonImporter.Side.EAST -> {
                val uv0 = vec2Of(face.uv.x, face.uv.y) / 16
                val uv1 = vec2Of(face.uv.z, face.uv.w) / 16
                Mesh(
                        pos = listOf(
                                vec3Of(p.x, p.y, n.z),
                                vec3Of(p.x, p.y, p.z),
                                vec3Of(p.x, n.y, p.z),
                                vec3Of(p.x, n.y, n.z)),
                        tex = listOf(
                                vec2Of(uv1.x, uv0.y),
                                vec2Of(uv0.x, uv0.y),
                                vec2Of(uv0.x, uv1.y),
                                vec2Of(uv1.x, uv1.y)
                        ),
                        faces = listOf(FaceIndex(listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
                )
            }
        }
    }

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