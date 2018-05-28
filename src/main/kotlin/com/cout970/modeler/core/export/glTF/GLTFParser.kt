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
            val buffers: PairList<GltfBuffer, ByteArray>,
            val bufferViews: PairList<GltfBufferView, ByteArray>,
            val accessors: PairList<GltfAccessor, List<*>>,
            val meshes: PairList<GltfMesh, ResultMesh>
    )

    data class ResultMesh(
            val primitives: PairList<GltfPrimitive, ResultPrimitive>
    )

    data class ResultPrimitive(
            val attributes: Map<GltfAttribute, Pair<GltfType, List<Any>>>,
            val indices: Pair<GltfType, List<Any>>?,
            val mode: GltfMode
    )

    fun parse(file: GltfFile, folder: ResourcePath): Result {

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
            val type = GltfComponentType.fromId(accessor.componentType)

            val buff = ByteBuffer.wrap(buffer, offset, buffer.size - offset).order(ByteOrder.LITTLE_ENDIAN)
            val list: List<Any> = intoList(accessor.type, type, accessor.count, buff)

            println("Loading Accessor[$index], of type: $type, grouped as ${accessor.type}, with data: $list")

            Pair(accessor, list)
        }

        val meshes = file.meshes.mapIndexed { index, mesh ->
            val primitives = mesh.primitives.map { prim ->

                val attr = prim.attributes.map { (k, v) ->
                    Pair(GltfAttribute.valueOf(k), accessors[v].first.type to accessors[v].second)
                }.toMap()

                val indices = prim.indices?.let { accessors[it] }?.let { (acc, list) ->
                    acc.type to list
                }

                val mode = GltfMode.fromId(prim.mode)

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

    private fun intoList(listType: GltfType, componentType: GltfComponentType, count: Int, buffer: ByteBuffer): List<Any> {
        val t = componentType
        val b = buffer
        return when (listType) {
            GltfType.SCALAR -> List(count) { b.next(t) }
            GltfType.VEC2 -> List(count) { vec2Of(b.next(t), b.next(t)) }
            GltfType.VEC3 -> List(count) { vec3Of(b.next(t), b.next(t), b.next(t)) }
            GltfType.VEC4 -> List(count) { vec4Of(b.next(t), b.next(t), b.next(t), b.next(t)) }
            GltfType.MAT2 -> TODO()
            GltfType.MAT3 -> TODO()
            GltfType.MAT4 -> TODO()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ByteBuffer.next(type: GltfComponentType): Number {
        return when (type) {
            GltfComponentType.BYTE, GltfComponentType.UNSIGNED_BYTE -> get()
            GltfComponentType.SHORT, GltfComponentType.UNSIGNED_SHORT -> short
            GltfComponentType.UNSIGNED_INT -> int
            GltfComponentType.FLOAT -> float
        }
    }
}