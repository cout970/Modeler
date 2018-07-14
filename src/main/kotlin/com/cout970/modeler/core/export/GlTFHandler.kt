package com.cout970.modeler.core.export

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.animation.Animation
import com.cout970.modeler.core.animation.Channel
import com.cout970.modeler.core.animation.Keyframe
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.export.glTF.*
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.util.toIQuaternion
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import com.google.gson.*
import org.joml.Quaterniond
import org.joml.Vector3d
import java.io.File
import java.lang.reflect.Type
import java.util.*

private val GSON = GsonBuilder()
        .registerTypeAdapter(IVector4::class.java, Vector4Serializer())
        .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
        .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
        .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
        .registerTypeAdapter(IMatrix4::class.java, Matrix4Serializer())
        .registerTypeAdapter(GltfAccessor::class.java, AccessorSerializer)
        .registerTypeAdapter(GltfBufferView::class.java, BufferViewSerializer)
        .registerTypeAdapter(List::class.java, EmptyListAdapter)
        .registerTypeAdapter(Map::class.java, EmptyMapAdapter)
        .setPrettyPrinting()
        .create()

private object EmptyListAdapter : JsonSerializer<List<*>> {

    override fun serialize(src: List<*>?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement? {
        if (src == null || src.isEmpty())
            return null

        return context.serialize(src)
    }
}

private object EmptyMapAdapter : JsonSerializer<Map<*, *>> {

    override fun serialize(src: Map<*, *>?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement? {
        if (src == null || src.isEmpty())
            return null

        return context.serialize(src)
    }
}

private object AccessorSerializer : JsonSerializer<GltfAccessor> {

    override fun serialize(src: GltfAccessor, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {

            addProperty("bufferView", src.bufferView)

            if (src.byteOffset != null && src.byteOffset != 0)
                addProperty("byteOffset", src.byteOffset)

            addProperty("componentType", src.componentType)

            if (src.normalized != null && src.normalized)
                addProperty("normalized", src.normalized)

            addProperty("count", src.count)

            add("type", context.serialize(src.type))

            if (src.max.isNotEmpty())
                add("max", context.serialize(src.max))

            if (src.min.isNotEmpty())
                add("min", context.serialize(src.min))

            if (src.sparse != null)
                add("sparse", context.serialize(src.sparse))

            if (src.name != null)
                addProperty("name", src.name)
        }
    }
}

private object BufferViewSerializer : JsonSerializer<GltfBufferView> {

    override fun serialize(src: GltfBufferView, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {

            addProperty("buffer", src.buffer)

            if (src.byteOffset != null && src.byteOffset != 0)
                addProperty("byteOffset", src.byteOffset)

            addProperty("byteLength", src.byteLength)

            if (src.byteStride != null)
                addProperty("byteStride", src.byteStride)

            if (src.target != null)
                addProperty("target", src.target)

            if (src.name != null)
                addProperty("name", src.name)
        }
    }
}

class GlTFExporter {

    fun export(file: File, _model: IModel) {
        val (model, buffer) = _model.toGlTF(file.nameWithoutExtension)
        File(file.parent, model.buffers[0].uri).writeBytes(buffer)
        file.writeBytes(GSON.toJson(model).toByteArray())
    }

    fun IModel.toGlTF(buffer: String) = glftModel {
        bufferName = "$buffer.bin"

        val groupToNode = mutableMapOf<AnimationTarget, UUID>()

        scene {

            node {
                name = "root"

                val tree = tree.toMutable()
                groupToNode += AnimationTargetGroup(tree.group) to id

                addSceneTree(this, this@toGlTF, tree, groupToNode)

                tree.objects.map { getObject(it) }.forEach {
                    node {
                        name = it.name
                        addMesh(this, it)
                    }
                }
            }
        }

        animationMap.values.forEach { anim ->
            animation {
                name = anim.name
                addAnimation(this, anim, groupToNode)
            }
        }
    }

    fun GLTFBuilder.addSceneTree(builder: GLTFBuilder.Node, model: IModel, tree: MutableGroupTree,
                                 groupToNode: MutableMap<AnimationTarget, UUID>): Unit = builder.run {
        node {
            name = model.getGroup(tree.group).name
            groupToNode += AnimationTargetGroup(tree.group) to id

            tree.children.forEach {
                addSceneTree(this, model, it, groupToNode)
            }

            tree.objects.map { model.getObject(it) }.forEach {
                node {
                    name = it.name
                    groupToNode += AnimationTargetObject(it.ref) to id
                    addMesh(this, it)
                }
            }
        }
    }

    fun GLTFBuilder.addAnimation(builder: GLTFBuilder.Animation, anim: IAnimation, groupToNode: Map<AnimationTarget, UUID>) = builder.apply {
        anim.channels.values.map { chan ->
            val groupRef = anim.channelMapping[chan.ref]
            val groupNode = groupToNode[groupRef] ?: return@map
            val useTranslation = chan.usesTranslation()
            val useRotation = chan.usesRotation()
            val useScale = chan.usesScale()

            if (!useTranslation && !useRotation && !useScale) return@map

            if (useTranslation)
                channel {
                    node = groupNode
                    transformType = TRANSLATION
                    timeValues = buffer(FLOAT, chan.keyframes.map { it.time })
                    transformValues = buffer(FLOAT, chan.keyframes.map {
                        val trans = it.value
                        when (trans) {
                            is TRSTransformation -> trans.translation
                            is TRTSTransformation -> trans.toTRS().translation
                            else -> error("Invalid transformation type: $trans")
                        }
                    })
                }


            if (useRotation)
                channel {
                    node = groupNode
                    transformType = ROTATION
                    timeValues = buffer(FLOAT, chan.keyframes.map { it.time })
                    transformValues = buffer(FLOAT, chan.keyframes.map {
                        val trans = it.value
                        when (trans) {
                            is TRSTransformation -> trans.rotation.toVector4()
                            is TRTSTransformation -> trans.toTRS().rotation.toVector4()
                            else -> error("Invalid transformation type: $trans")
                        }
                    })
                }

            if (useScale)
                channel {
                    node = groupNode
                    transformType = SCALE
                    timeValues = buffer(FLOAT, chan.keyframes.map { it.time })
                    transformValues = buffer(FLOAT, chan.keyframes.map {
                        val trans = it.value
                        when (trans) {
                            is TRSTransformation -> trans.scale
                            is TRTSTransformation -> trans.scale
                            else -> error("Invalid transformation type: $trans")
                        }
                    })
                }
        }
    }

    fun GLTFBuilder.addMesh(node: GLTFBuilder.Node, obj: IObject) = node.apply {
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

    fun IChannel.usesTranslation() = keyframes.any {
        val t = it.value
        when (t) {
            is TRSTransformation -> t.translation != Vector3.ZERO
            is TRTSTransformation -> t.toTRS().translation != Vector3.ZERO
            else -> false
        }
    }

    fun IChannel.usesRotation() = keyframes.any {
        val t = it.value
        when (t) {
            is TRSTransformation -> t.rotation != Quaternion.IDENTITY
            is TRTSTransformation -> t.toTRS().rotation != Quaternion.IDENTITY
            else -> false
        }
    }

    fun IChannel.usesScale() = keyframes.any {
        val t = it.value
        when (t) {
            is TRSTransformation -> t.scale != Vector3.ONE
            is TRTSTransformation -> t.toTRS().scale != Vector3.ONE
            else -> false
        }
    }

    fun IQuaternion.toVector4() = vec4Of(x, y, z, w)
}

class GlTFImporter {

    fun import(path: ResourcePath): IModel {
        val file = GSON.fromJson(path.inputStream().reader(), GltfFile::class.java)
        val extraData = GLTFParser.parse(file, path.parent!!)
        val scene = extraData.scenes[file.scene ?: 0]

        val nodes = scene.second.nodes

        if (nodes.isEmpty()) {
            return Model.empty()
        }

        return if (nodes.size == 1 && nodes[0].second.mesh == null) {
            parseScene(extraData, nodes[0].second.children)
        } else {
            parseScene(extraData, nodes)
        }
    }

    fun parseScene(data: GLTFParser.Result, nodes: List<Pair<GltfNode, GLTFParser.ResultNode>>): IModel {
        val objs = mutableMapOf<IObjectRef, IObject>()
        val materials = mutableMapOf<IMaterialRef, IMaterial>()
        val groups = mutableMapOf<IGroupRef, IGroup>()
        val animations = mutableMapOf<IAnimationRef, IAnimation>()
        val root = MutableGroupTree(RootGroupRef)
        val nodeMapping = mutableMapOf<Int, Any>()

        nodes.forEach { (glNode, resNode) ->
            processNode(glNode, resNode, root, objs, materials, groups, nodeMapping)
        }

        val channelMapping = mutableMapOf<IChannelRef, AnimationTarget>()

        data.animations.forEach { (gltf, anim) ->
            val channels = anim.channels.map { (gl, channel) ->
                val keyframes = channel.times.mapIndexed { index, time ->
                    Keyframe(
                            time,
                            transformationOf(channel.values[index], channel.path)
                    )
                }

                val chan = Channel(
                        name = "Channel",
                        interpolation = InterpolationMethod.LINEAR,
                        keyframes = keyframes
                )

                val thing = nodeMapping[gl.target.node]

                (thing as? IGroupRef)?.let { channelMapping += chan.ref to AnimationTargetGroup(it) }
                (thing as? IObjectRef)?.let { channelMapping += chan.ref to AnimationTargetObject(it) }

                chan
            }

            val a = Animation(
                    channels.associateBy { it.ref },
                    channelMapping,
                    channels.map { it.keyframes.last().time }.max() ?: 1f,
                    gltf.name ?: "Animation"
            )
            animations += a.ref to a
        }

        return Model.of(objs, materials, groups, animations, root.toImmutable())
    }

    fun transformationOf(value: Any, type: GltfChannelPath): ITransformation = when (type) {
        GltfChannelPath.translation -> TRSTransformation(translation = value as IVector3)
        GltfChannelPath.rotation -> TRSTransformation(rotation = (value as IVector4).toQuat())
        GltfChannelPath.scale -> TRSTransformation(scale = value as IVector3)
        GltfChannelPath.weights -> error("Not supported weights in skinning animation")
    }

    fun IVector4.toQuat() = quatOf(x, y, z, w)

    fun processNode(gltfNode: GltfNode, node: GLTFParser.ResultNode, root: MutableGroupTree,
                    objs: MutableMap<IObjectRef, IObject>,
                    materials: MutableMap<IMaterialRef, IMaterial>,
                    groups: MutableMap<IGroupRef, IGroup>,
                    nodeMapping: MutableMap<Int, Any>) {

        val mesh = node.mesh
        if (mesh != null) {
            val obj = parseObj(transformOf(gltfNode), mesh.second, gltfNode.name ?: "Obj")
            root.objects += obj.ref
            objs += obj.ref to obj
            nodeMapping += node.index to obj.ref
        } else {
            val group = Group(gltfNode.name ?: "Group", transform = transformOf(gltfNode))
            val tree = MutableGroupTree(group.ref)

            root.children += tree
            groups += group.ref to group
            nodeMapping += node.index to group.ref

            node.children.forEach { (glNode, resNode) ->
                processNode(glNode, resNode, tree, objs, materials, groups, nodeMapping)
            }
        }
    }

    private fun transformOf(gltfNode: GltfNode): ITransformation {
        val trans = TRSTransformation(
                gltfNode.translation?.times(16) ?: Vector3.ZERO,
                gltfNode.rotation ?: Quaternion.IDENTITY,
                gltfNode.scale ?: Vector3.ONE
        )

        if (gltfNode.matrix != null) {
            val joml = gltfNode.matrix.toJOML()

            val pos = joml.getTranslation(Vector3d()).toIVector() * 16
            val quat = Quaterniond().setFromUnnormalized(joml).toIQuaternion()
            val scale = joml.getScale(Vector3d()).toIVector()

            return trans.merge(TRSTransformation(pos, quat, scale))
        }

        return trans
    }

    fun parseObj(transform: ITransformation, data: GLTFParser.ResultMesh, name: String): IObject {
        val meshes: List<IMesh> = data.primitives.map { (_, primData) ->

            if (GltfAttribute.POSITION !in primData.attributes) {
                error("Found mesh without POSITION attribute")
            }

            val vecPos = getPositions(primData).map { it * 16 }
            val vecUv = getUv(primData)
            val indices = primData.indices?.let { getIndices(it) } ?: vecPos.indices

            if (vecUv != null) {
                check(vecPos.size == vecUv.size) { "Different sizes: ${vecPos.size} : ${vecUv.size}" }
            }

            val faces = when (primData.mode) {
                GltfMode.TRIANGLES -> {
                    indices.windowed(3, 3).map {
                        FaceIndex(listOf(it[0], it[1], it[2], it[2]), listOf(it[0], it[1], it[2], it[2]))
                    }
                }
                GltfMode.QUADS -> {
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

        return Object(
                name = name,
                mesh = meshes.parallelStream().reduce { acc, other -> acc.merge(other) }.get(),
                transformation = transform
        )
    }

    private fun getPositions(data: GLTFParser.ResultPrimitive): List<IVector3> {
        val (posType, pos) = data.attributes[GltfAttribute.POSITION]!!

        return when (posType) {
            GltfType.SCALAR -> pos.filterIsInstance<Number>().map { vec3Of(it, 0, 0) }
            GltfType.VEC2 -> pos.filterIsInstance<IVector2>().map { vec3Of(it.x, it.y, 0) }
            GltfType.VEC3 -> pos.filterIsInstance<IVector3>().map { vec3Of(it.x, it.y, it.z) }
            GltfType.VEC4 -> pos.filterIsInstance<IVector4>().map { vec3Of(it.x, it.y, it.z) }
            GltfType.MAT2, GltfType.MAT3, GltfType.MAT4 -> error("Trying to use a matrix as vertex position")
        }
    }

    private fun getUv(data: GLTFParser.ResultPrimitive): List<IVector2>? {
        val (uvType, uv) = data.attributes[GltfAttribute.TEXCOORD_0] ?: return null

        return when (uvType) {
            GltfType.SCALAR -> uv.filterIsInstance<Number>().map { vec2Of(it, 0) }
            GltfType.VEC2 -> uv.filterIsInstance<IVector2>().map { vec2Of(it.x, it.y) }
            GltfType.VEC3 -> uv.filterIsInstance<IVector3>().map { vec2Of(it.x, it.y) }
            GltfType.VEC4 -> uv.filterIsInstance<IVector4>().map { vec2Of(it.x, it.y) }
            GltfType.MAT2, GltfType.MAT3, GltfType.MAT4 -> error("Trying to use a matrix as vertex position")
        }
    }

    private fun getIndices(pair: Pair<GltfType, List<Any>>): List<Int> {
        val (type, list) = pair

        return when (type) {
            GltfType.SCALAR -> list.filterIsInstance<Number>().map { it.toInt() }
            GltfType.VEC2, GltfType.VEC3, GltfType.VEC4,
            GltfType.MAT2, GltfType.MAT3, GltfType.MAT4 -> error("Trying to use a matrix as vertex position")
        }
    }
}



