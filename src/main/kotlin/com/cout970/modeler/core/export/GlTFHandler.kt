package com.cout970.modeler.core.export

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.animation.*
import com.cout970.modeler.core.export.glTF.*
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.`object`.ObjectNone
import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.toIQuaternion
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import com.google.gson.*
import org.apache.commons.io.FilenameUtils
import org.joml.Quaterniond
import org.joml.Vector3d
import java.io.File
import java.io.IOException
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
        val (model, buffer, images) = ModelToGltf(_model, file.nameWithoutExtension).build()
        File(file.parent, model.buffers[0].uri).writeBytes(buffer)
        file.writeBytes(GSON.toJson(model).toByteArray())

        images.forEach { (name, path) ->
            try {
                File(file.parentFile, name).writeBytes(path.inputStream().readBytes())
            } catch (e: IOException) {
                e.print()
            }
        }
    }

    @Suppress("ArrayInDataClass")
    data class GltfModel(val file: GltfFile, val buffer: ByteArray, val images: List<Pair<String, ResourcePath>>)

    class ModelToGltf(val model: IModel, val prefix: String) {

        val targetToNode = mutableMapOf<AnimationTarget, UUID>()
        val pathToImg = mutableMapOf<String, ResourcePath>()
        val definedMaterials = mutableSetOf<IMaterialRef>()

        fun build(): GltfModel {

            val (model, buffer) = glftModel {
                bufferName = "$prefix.bin"

                scene {

                    node {
                        name = "root"

                        val tree = model.tree.toMutable()
                        targetToNode += AnimationTargetGroup(tree.group) to id

                        tree.children.forEach {
                            addSceneTree(this, it)
                        }

                        tree.objects.map { model.getObject(it) }
                                .forEach { addObject(this, it) }
                    }
                }

                model.animationMap.values.forEach { anim ->
                    addAnimation(anim)
                }
            }

            return GltfModel(model, buffer, pathToImg.toList())
        }

        fun GLTFBuilder.addSceneTree(builder: GLTFBuilder.Node, tree: MutableGroupTree): Unit = builder.run {

            node {
                val group = model.getGroup(tree.group)
                name = group.name

                val transform = group.transform.toTRS()
                if (transform.matrix != Matrix4.IDENTITY) {
                    transformation = GLTFBuilder.Transformation.TRS(
                            transform.translation * 0.0625f,
                            transform.rotation,
                            transform.scale
                    )
                }

                targetToNode += AnimationTargetGroup(tree.group) to id

                tree.children.forEach {
                    addSceneTree(this, it)
                }

                tree.objects.map { model.getObject(it) }.forEach { addObject(this, it) }
            }
        }

        fun GLTFBuilder.addObject(parent: GLTFBuilder.Node, it: IObject) = parent.run {
            node {
                name = it.name

                val objTransform = it.transformation.toTRS()
                if (objTransform.matrix != Matrix4.IDENTITY) {
                    transformation = GLTFBuilder.Transformation.TRS(
                            objTransform.translation * 0.0625f,
                            objTransform.rotation,
                            objTransform.scale
                    )
                }

                targetToNode += AnimationTargetObject(it.ref) to id
                addMesh(this, it)
            }
        }

        fun GLTFBuilder.addAnimation(anim: IAnimation) {

            val used = anim.channels.values.any { chan ->
                val target = anim.channelMapping[chan.ref] ?: return@any false
                targetToNode[target] ?: return@any false

                chan.usesTranslation() || chan.usesRotation() || chan.usesScale()
            }

            if (!used) return

            animation {
                name = anim.name
                anim.channels.values.map { chan ->
                    val target = anim.channelMapping[chan.ref] ?: return@map
                    val groupNode = targetToNode[target] ?: return@map

                    val useTranslation = chan.usesTranslation()
                    val useRotation = chan.usesRotation()
                    val useScale = chan.usesScale()

                    if (!useTranslation && !useRotation && !useScale) return@map

                    val keyframeValues = chan.keyframes.map {
                        Animator.combine(target.getTransformation(model), it.value)
                    }

                    if (useTranslation)
                        channel {
                            node = groupNode
                            transformType = TRANSLATION
                            timeValues = buffer(FLOAT, chan.keyframes.map { it.time })
                            transformValues = buffer(FLOAT, keyframeValues.map { it.translation * 0.0625 })
                        }


                    if (useRotation)
                        channel {
                            node = groupNode
                            transformType = ROTATION
                            timeValues = buffer(FLOAT, chan.keyframes.map { it.time })
                            transformValues = buffer(FLOAT, keyframeValues.map { it.rotation.toVector4() })
                        }

                    if (useScale)
                        channel {
                            node = groupNode
                            transformType = SCALE
                            timeValues = buffer(FLOAT, chan.keyframes.map { it.time })
                            transformValues = buffer(FLOAT, keyframeValues.map { it.scale })
                        }
                }
            }
        }

        fun GLTFBuilder.addMesh(node: GLTFBuilder.Node, obj: IObject) = node.apply {
            if (obj == ObjectNone) return@apply

            mesh {

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

                    if (vertexPos.isEmpty()) {
                        log(Level.WARNING) { "Exporting primitive with empty mesh" }
                    }
                    attributes[POSITION] = buffer(FLOAT, vertexPos)
                    attributes[TEXCOORD_0] = buffer(FLOAT, vertexTex)

                    if (obj.material != MaterialRefNone) {
                        addMaterial(this, obj)
                    }
                }
            }
        }

        fun GLTFBuilder.addMaterial(builder: GLTFBuilder.Primitive, obj: IObject) = builder.apply {
            val material = model.getMaterial(obj.material)

            when (material) {
                is TexturedMaterial -> {
                    material {
                        id = material.ref.materialId

                        if (material.ref !in definedMaterials) {
                            definedMaterials += material.ref

                            pbrMetallicRoughness {
                                baseColor {
                                    val name = "${prefix}_${FilenameUtils.getName(material.path.uri.toURL().path)}"
                                    var count = 1
                                    var finalName = name
                                    while (finalName in pathToImg) { // || File(folder, finalName).exists()) {
                                        finalName = "${name}_$count"
                                        count++
                                    }
                                    pathToImg[finalName] = material.path
                                    uri = name
                                }
                            }
                        }
                    }
                }

                is ColoredMaterial -> {
                    material {
                        id = material.ref.materialId

                        if (material.ref !in definedMaterials) {
                            definedMaterials += material.ref

                            pbrMetallicRoughness {
                                baseColorFactor = material.color.toVector4(1.0)
                            }
                        }
                    }
                }
            }
        }

        // TODO fix this
        fun IChannel.usesTranslation() = keyframes.any {
            it.value.toTRS().translation != Vector3.ZERO
        }

        fun IChannel.usesRotation() = keyframes.any {
            it.value.toTRS().rotation != Quaternion.IDENTITY
        }

        fun IChannel.usesScale() = keyframes.any {
            it.value.toTRS().scale != Vector3.ONE
        }

        fun IQuaternion.toVector4() = vec4Of(x, y, z, w)
    }
}

class GlTFImporter {

    fun import(path: ResourcePath): IModel {
        val file = GSON.fromJson(path.inputStream().reader(), GltfFile::class.java)
        val extraData = GLTFParser.parse(file, path.parent!!)
        val scene = extraData.scenes[file.scene ?: 0]

        val nodes = scene.second.nodes.filter { it.first.camera == null }


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

    fun transformationOf(value: Any, type: GltfChannelPath): TRSTransformation = when (type) {
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
            val name = gltfNode.name ?: mesh.first.name ?: "Obj"
            val obj = parseObj(transformOf(gltfNode), mesh.second, name)
            root.objects += obj.ref
            objs += obj.ref to obj
            nodeMapping += node.index to obj.ref
        } else {
            val name = gltfNode.name ?: "Group"
            val group = Group(name, transform = transformOf(gltfNode))
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
        if (gltfNode.matrix != null) {
            val joml = gltfNode.matrix.toJOML()

            // Note the 16x scale is important
            val pos = joml.getTranslation(Vector3d()).toIVector() * 16
            val quaternion = Quaterniond().setFromUnnormalized(joml).toIQuaternion()
            val scale = joml.getScale(Vector3d()).toIVector()

            return TRSTransformation(pos, quaternion, scale)
        }

        return TRSTransformation(
                gltfNode.translation?.times(16) ?: Vector3.ZERO,
                gltfNode.rotation ?: Quaternion.IDENTITY,
                gltfNode.scale ?: Vector3.ONE
        )
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
                        FaceIndex.from(listOf(it[0], it[1], it[2], it[2]), listOf(it[0], it[1], it[2], it[2]))
                    }
                }
                GltfMode.QUADS -> {
                    indices.windowed(4, 4).map {
                        FaceIndex.from(it, it)
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



