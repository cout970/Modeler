package com.cout970.modeler.core.export.glTF

import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.extensions.vec4Of
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

typealias PairList<A, B> = List<Pair<A, B>>

object GLTFParser {

    data class Result(
            val buffers: PairList<GLTF.Buffer, ByteArray>,
            val bufferViews: PairList<GLTF.BufferView, ByteArray>,
            val accessors: PairList<GLTF.Accessor, List<*>>,
            val meshes: PairList<GLTF.Mesh, ResultMesh>
    )

    data class ResultMesh(
            val primitives: PairList<GLTF.Primitive, ResultPrimitive>
    )

    data class ResultPrimitive(
            val attributes: Map<GLTF.Attribute, Pair<GLTF.Type, List<Any>>>,
            val indices: Pair<GLTF.Type, List<Any>>?,
            val mode: GLTF.GLMode
    )

    fun parse(file: GLTF.File, folder: ResourcePath): Result {

        val buffers = file.buffers.map { buff ->

            val uri = buff.uri ?: error("Found buffer without uri, unable to load, buffer: $buff")
            val bytes = folder.resolve(uri).inputStream().readBytes()

            if (bytes.size != buff.byteLength) {
                error("Buffer byteLength, and resource size doesn't match, buffer: $buff, resource size: ${bytes.size}")
            }

            println("Loaded file '${buff.uri}' with ${buff.byteLength} bytes")

            Pair(buff, bytes)
        }

        val bufferViews = file.bufferViews.mapIndexed { index, view ->

            val (_, buffer) = buffers[view.buffer]
            val offset = view.byteOffset ?: 0
            val size = view.byteLength

            val bufferData = Arrays.copyOfRange(buffer, offset, offset + size)

            println("Loaded bufferViews[$index] at buffer: ${view.buffer}, with size: $size, offset: $offset")

            Pair(view, bufferData)
        }

        val accessors = file.accessors.mapIndexed { index, accessor ->

            val viewIndex = accessor.bufferView ?: error("Unsupported Empty BufferView at accessor: $accessor")

            val (_, buffer) = bufferViews[viewIndex]

            val offset = accessor.byteOffset ?: 0
            val type = GLTF.ComponentType.fromId(accessor.componentType)

            val buff = ByteBuffer.wrap(buffer, offset, buffer.size - offset).order(ByteOrder.LITTLE_ENDIAN)
            val list: List<Any> = intoList(accessor.type, type, accessor.count, buff)

            println("Loading Accessor[$index], of type: $type, grouped as ${accessor.type}, with data: $list")

            Pair(accessor, list)
        }

        val meshes = file.meshes.mapIndexed { index, mesh ->
            val primitives = mesh.primitives.map { prim ->

                val attr = prim.attributes.map { (k, v) ->
                    Pair(GLTF.Attribute.valueOf(k), accessors[v].first.type to accessors[v].second)
                }.toMap()

                val indices = prim.indices?.let { accessors[it] }?.let { (acc, list) ->
                    acc.type to list
                }

                val mode = GLTF.GLMode.fromId(prim.mode)

                Pair(prim, ResultPrimitive(attr, indices, mode))
            }

            println("Loading Mesh[$index], name: ${mesh.name}")

            Pair(mesh, ResultMesh(primitives))
        }

        return Result(
                buffers = buffers,
                bufferViews = bufferViews,
                accessors = accessors,
                meshes = meshes
        )
    }

    private fun intoList(listType: GLTF.Type, componentType: GLTF.ComponentType, count: Int, buffer: ByteBuffer): List<Any> {
        val t = componentType
        val b = buffer
        return when (listType) {
            GLTF.Type.SCALAR -> List(count) { b.next(t) }
            GLTF.Type.VEC2 -> List(count) { vec2Of(b.next(t), b.next(t)) }
            GLTF.Type.VEC3 -> List(count) { vec3Of(b.next(t), b.next(t), b.next(t)) }
            GLTF.Type.VEC4 -> List(count) { vec4Of(b.next(t), b.next(t), b.next(t), b.next(t)) }
            GLTF.Type.MAT2 -> TODO()
            GLTF.Type.MAT3 -> TODO()
            GLTF.Type.MAT4 -> TODO()
        }
    }

    private inline fun ByteBuffer.next(type: GLTF.ComponentType): Number {
        return when (type) {
            GLTF.ComponentType.BYTE, GLTF.ComponentType.UNSIGNED_BYTE -> get()
            GLTF.ComponentType.SHORT, GLTF.ComponentType.UNSIGNED_SHORT -> short
            GLTF.ComponentType.UNSIGNED_INT -> int
            GLTF.ComponentType.FLOAT -> float
        }
    }
}