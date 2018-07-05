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
            val meshes: PairList<GltfMesh, ResultMesh>,
            val scenes: PairList<GltfScene, ResultScene>,
            val animations: PairList<GltfAnimation, ResultAnimation>
    )

    data class ResultMesh(
            val primitives: PairList<GltfPrimitive, ResultPrimitive>
    )

    data class ResultPrimitive(
            val attributes: Map<GltfAttribute, Pair<GltfType, List<Any>>>,
            val indices: Pair<GltfType, List<Any>>?,
            val mode: GltfMode
    )

    data class ResultNode(
            val index: Int,
            val children: PairList<GltfNode, ResultNode>,
            val mesh: Pair<GltfMesh, ResultMesh>?
    )

    data class ResultScene(
            val nodes: PairList<GltfNode, ResultNode>
    )

    data class ResultAnimation(
            val channels: PairList<GltfAnimationChannel, ResultChannel>
    )

    data class ResultChannel(
            val node: Int,
            val path: GltfChannelPath,
            val times: List<Float>,
            val interpolation: GltfInterpolation,
            val values: List<Any>
    )

    private fun parseBuffers(file: GltfFile, folder: ResourcePath): PairList<GltfBuffer, ByteArray> {
        return file.buffers.map { buff ->

            val uri = buff.uri ?: error("Found buffer without uri, unable to load, buffer: $buff")
            val bytes = folder.resolve(uri).inputStream().readBytes()

            if (bytes.size != buff.byteLength) {
                error("Buffer byteLength, and resource size doesn't match, buffer: $buff, resource size: ${bytes.size}")
            }

            println("Loaded file '${buff.uri}' with ${buff.byteLength} bytes")

            Pair(buff, bytes)
        }
    }

    private fun parseBufferViews(file: GltfFile, buffers: PairList<GltfBuffer, ByteArray>): PairList<GltfBufferView, ByteArray> {
        return file.bufferViews.mapIndexed { index, view ->

            val (_, buffer) = buffers[view.buffer]
            val offset = view.byteOffset ?: 0
            val size = view.byteLength

            val bufferData = Arrays.copyOfRange(buffer, offset, offset + size)

            println("Loaded bufferViews[$index] at buffer: ${view.buffer}, with size: $size, offset: $offset")

            Pair(view, bufferData)
        }
    }

    private fun parseAccessors(file: GltfFile, bufferViews: PairList<GltfBufferView, ByteArray>): PairList<GltfAccessor, List<Any>> {
        return file.accessors.mapIndexed { index, accessor ->

            val viewIndex = accessor.bufferView ?: error("Unsupported Empty BufferView at accessor: $accessor")

            val (_, buffer) = bufferViews[viewIndex]

            val offset = accessor.byteOffset ?: 0
            val type = GltfComponentType.fromId(accessor.componentType)

            val buff = ByteBuffer.wrap(buffer, offset, buffer.size - offset).order(ByteOrder.LITTLE_ENDIAN)
            val list: List<Any> = intoList(accessor.type, type, accessor.count, buff)

            println("Loading Accessor[$index], of type: $type, grouped as ${accessor.type}, with data: $list")

            Pair(accessor, list)
        }
    }

    private fun intoList(listType: GltfType, componentType: GltfComponentType, count: Int, buffer: ByteBuffer): List<Any> {
        val t = componentType
        val b = buffer
        return when (listType) {
            GltfType.SCALAR -> List(count) { b.next(t) }
            GltfType.VEC2 -> List(count) { vec2Of(b.next(t), b.next(t)) }
            GltfType.VEC3 -> List(count) { vec3Of(b.next(t), b.next(t), b.next(t)) }
            GltfType.VEC4 -> List(count) { vec4Of(b.next(t), b.next(t), b.next(t), b.next(t)) }
            GltfType.MAT2 -> error("Unsupported")
            GltfType.MAT3 -> error("Unsupported")
            GltfType.MAT4 -> error("Unsupported")
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

    private fun parseMeshes(file: GltfFile, accessors: PairList<GltfAccessor, List<Any>>): PairList<GltfMesh, ResultMesh> {
        return file.meshes.mapIndexed { index, mesh ->
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
    }

    private fun parseScenes(file: GltfFile, meshes: PairList<GltfMesh, ResultMesh>): PairList<GltfScene, ResultScene> {
        return file.scenes.map { scene ->
            val nodes = scene.nodes ?: emptyList()
            val parsedNodes = nodes.map { file.nodes[it] to parseNode(file, it, file.nodes[it], meshes) }

            Pair(scene, ResultScene(parsedNodes))
        }
    }

    private fun parseNode(file: GltfFile, nodeIndex: Int, node: GltfNode, meshes: PairList<GltfMesh, ResultMesh>): ResultNode {
        val children = node.children.map { file.nodes[it] to parseNode(file, it, file.nodes[it], meshes) }
        val mesh = node.mesh?.let { meshes[it] }

        return ResultNode(nodeIndex, children, mesh)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseChannel(channel: GltfAnimationChannel, samplers: List<GltfAnimationSampler>,
                             accessors: PairList<GltfAccessor, List<Any>>): Pair<GltfAnimationChannel, ResultChannel> {
        val sampler = samplers[channel.sampler]
        val timeValues = accessors[sampler.input].second.map { (it as Number).toFloat() }

        val res = ResultChannel(
                node = channel.target.node,
                path = GltfChannelPath.valueOf(channel.target.path),
                times = timeValues,
                interpolation = sampler.interpolation,
                values = accessors[sampler.output].second
        )

        return channel to res
    }

    private fun parseAnimations(file: GltfFile, accessors: PairList<GltfAccessor, List<Any>>): PairList<GltfAnimation, ResultAnimation> {
        return file.animations.map { anim ->
            val channels = anim.channels.map { parseChannel(it, anim.samplers, accessors) }
            anim to ResultAnimation(channels)
        }
    }

    fun parse(file: GltfFile, folder: ResourcePath): Result {

        val buffers = parseBuffers(file, folder)
        val bufferViews = parseBufferViews(file, buffers)
        val accessors = parseAccessors(file, bufferViews)
        val meshes = parseMeshes(file, accessors)
        val scenes = parseScenes(file, meshes)
        val animations = parseAnimations(file, accessors)

        return Result(
                buffers = buffers,
                bufferViews = bufferViews,
                accessors = accessors,
                meshes = meshes,
                scenes = scenes,
                animations = animations
        )
    }
}