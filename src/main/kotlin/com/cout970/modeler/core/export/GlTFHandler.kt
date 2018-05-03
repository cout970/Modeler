package com.cout970.modeler.core.export

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.glTF.GLTF
import com.cout970.modeler.core.export.glTF.GLTF.Attribute.POSITION
import com.cout970.modeler.core.export.glTF.GLTF.Attribute.TEXCOORD_0
import com.cout970.modeler.core.export.glTF.GLTFParser
import com.cout970.modeler.core.export.glTF.glftModel
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.google.gson.GsonBuilder
import java.io.File

private val GSON = GsonBuilder()
        .registerTypeAdapter(IVector4::class.java, Vector4Serializer())
        .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
        .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
        .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
        .registerTypeAdapter(IMatrix4::class.java, Matrix4Serializer())
        .create()

class GlTFExporter {

    fun export(file: File, _model: IModel) {
        val (model, buffer) = _model.toGlTF(file.nameWithoutExtension)
        File(file.parent, model.buffers[0].uri).writeBytes(buffer)
        file.writeBytes(GSON.toJson(model).toByteArray())
    }

    fun IModel.toGlTF(buffer: String) = glftModel {
        bufferName = buffer

        objectMap.values.forEach { obj ->
            node {
                name = obj.name

                mesh {
                    name = obj.name

                    primitive {

                        val pos = obj.mesh.pos
                        val tex = obj.mesh.tex
                        val scale = 1f / 16f

                        val vertexPos = obj.mesh.faces.flatMap { face ->
                            when (face.vertexCount) {
                                3 -> listOf(
                                        pos[face.pos[0]] * scale,
                                        pos[face.pos[1]] * scale,
                                        pos[face.pos[2]] * scale
                                )
                                4 -> listOf(
                                        pos[face.pos[0]] * scale,
                                        pos[face.pos[1]] * scale,
                                        pos[face.pos[2]] * scale,

                                        pos[face.pos[0]] * scale,
                                        pos[face.pos[2]] * scale,
                                        pos[face.pos[3]] * scale
                                )
                                else -> error("Invalid vertexCount: ${face.vertexCount}")
                            }
                        }
                        val vertexTex = obj.mesh.faces.flatMap { face ->
                            when (face.vertexCount) {
                                3 -> listOf(
                                        tex[face.tex[0]],
                                        tex[face.tex[1]],
                                        tex[face.tex[2]]
                                )
                                4 -> listOf(
                                        tex[face.tex[0]],
                                        tex[face.tex[1]],
                                        tex[face.tex[2]],

                                        tex[face.tex[0]],
                                        tex[face.tex[2]],
                                        tex[face.tex[3]]
                                )
                                else -> error("Invalid vertexCount: ${face.vertexCount}")
                            }
                        }

                        attributes[POSITION] = buffer(FLOAT, vertexPos)
                        attributes[TEXCOORD_0] = buffer(FLOAT, vertexTex)
                    }
                }
            }
        }
    }
}

class GlTFImporter {

    fun import(path: ResourcePath): IModel {
        val file = GSON.fromJson(path.inputStream().reader(), GLTF.File::class.java)
        val extraData = GLTFParser.parse(file, path.parent!!)

        val objs: List<IObject> = extraData.meshes.mapNotNull { (mesh, data) ->

            val meshes: List<IMesh> = data.primitives.map { (prim, data) ->

                if (POSITION !in data.attributes) return@mapNotNull null

                val vecPos = getPositions(data).map { it * 16f }
                val vecUv = getUv(data)
                val indices = data.indices?.let { getIndices(it) } ?: vecPos.indices

                if (vecUv != null) {
                    check(vecPos.size == vecUv.size) { "Different sizes: ${vecPos.size} : ${vecUv.size}" }
                }

                val faces = when (data.mode) {
                    GLTF.GLMode.TRIANGLES -> {
                        indices.windowed(3, 3).map {
                            FaceIndex(listOf(it[0], it[1], it[2], it[2]), listOf(it[0], it[1], it[2], it[2]))
                        }
                    }
                    GLTF.GLMode.QUADS -> {
                        indices.windowed(4, 4).map {
                            FaceIndex(it, it)
                        }
                    }
                    else -> error("Invalid mode")
                }

                if (vecUv != null) {
                    Mesh(vecPos, vecUv, faces)
                } else {
                    Mesh(vecPos, vecPos.map { Vector2.ORIGIN }, faces)
                }
            }

            Object(
                    name = mesh.name ?: "Obj",
                    mesh = meshes.reduce { acc, other -> acc.merge(other) }
            )
        }

        return Model.of(objects = objs)
    }

    private fun getPositions(data: GLTFParser.ResultPrimitive): List<IVector3> {
        val (posType, pos) = data.attributes[POSITION]!!

        return when (posType) {
            GLTF.Type.SCALAR -> pos.filterIsInstance<Number>().map { vec3Of(it, 0, 0) }
            GLTF.Type.VEC2 -> pos.filterIsInstance<IVector2>().map { vec3Of(it.x, it.y, 0) }
            GLTF.Type.VEC3 -> pos.filterIsInstance<IVector3>().map { vec3Of(it.x, it.y, it.z) }
            GLTF.Type.VEC4 -> pos.filterIsInstance<IVector4>().map { vec3Of(it.x, it.y, it.z) }
            GLTF.Type.MAT2, GLTF.Type.MAT3, GLTF.Type.MAT4 -> error("Trying to use a matrix as vertex position")
        }
    }

    private fun getUv(data: GLTFParser.ResultPrimitive): List<IVector2>? {
        val (uvType, uv) = data.attributes[TEXCOORD_0] ?: return null

        return when (uvType) {
            GLTF.Type.SCALAR -> uv.filterIsInstance<Number>().map { vec2Of(it, 0) }
            GLTF.Type.VEC2 -> uv.filterIsInstance<IVector2>().map { vec2Of(it.x, it.y) }
            GLTF.Type.VEC3 -> uv.filterIsInstance<IVector3>().map { vec2Of(it.x, it.y) }
            GLTF.Type.VEC4 -> uv.filterIsInstance<IVector4>().map { vec2Of(it.x, it.y) }
            GLTF.Type.MAT2, GLTF.Type.MAT3, GLTF.Type.MAT4 -> error("Trying to use a matrix as vertex position")
        }
    }

    private fun getIndices(pair: Pair<GLTF.Type, List<Any>>): List<Int> {
        val (type, list) = pair

        return when (type) {
            GLTF.Type.SCALAR -> list.filterIsInstance<Number>().map { it.toInt() }
            GLTF.Type.VEC2, GLTF.Type.VEC3, GLTF.Type.VEC4,
            GLTF.Type.MAT2, GLTF.Type.MAT3, GLTF.Type.MAT4 -> error("Trying to use a matrix as vertex position")
        }
    }
}



