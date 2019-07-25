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
import com.cout970.modeler.core.animation.Animation
import com.cout970.modeler.core.animation.Channel
import com.cout970.modeler.core.animation.Keyframe
import com.cout970.modeler.core.animation.ref
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
import com.cout970.modeler.util.toQuaternion
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import com.google.gson.*
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
        File(file.parent, model.buffers[0].uri!!).writeBytes(buffer)
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

        val objToWrapper = mutableMapOf<IObjectRef, UUID>()
        val objToWrapper2 = mutableMapOf<IObjectRef, UUID>()
        val objToNode = mutableMapOf<IObjectRef, UUID>()
        val groupToWrapper = mutableMapOf<IGroupRef, UUID>()
        val groupToWrapper2 = mutableMapOf<IGroupRef, UUID>()
        val groupToNode = mutableMapOf<IGroupRef, UUID>()

        val pathToImg = mutableMapOf<String, ResourcePath>()
        val definedMaterials = mutableSetOf<IMaterialRef>()
        lateinit var animatedObjects: Set<IObjectRef>
        lateinit var animatedGroups: Set<IGroupRef>

        fun build(): GltfModel {
            animatedObjects = model.animationMap.values
                .flatMap { anim -> anim.channels.values.filter { it.type == ChannelType.ROTATION }.map { anim.channelMapping[it.ref] } }
                .mapNotNull { (it as? AnimationTargetObject)?.refs }
                .flatten()
                .toSet()

            animatedGroups = model.animationMap.values
                .flatMap { anim -> anim.channels.values.filter { it.type == ChannelType.ROTATION }.map { anim.channelMapping[it.ref] } }
                .mapNotNull { (it as? AnimationTargetGroup)?.ref }
                .toSet()

            val (model, buffer) = glftModel {
                bufferName = "$prefix.bin"

                scene {

                    node {
                        name = "root"

                        val tree = model.tree.toMutable()
                        groupToNode[tree.group] = id

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

        fun GLTFBuilder.addSceneTree(builder: GLTFBuilder.Node, tree: MutableGroupTree) {

            if (tree.group in animatedGroups) {
                with(builder) {
                    val group = model.getGroup(tree.group)
                    node {
                        name = "${group.name}_animation_wrapper_2"
                        groupToWrapper2[tree.group] = id

                        node {
                            name = "${group.name}_animation_wrapper"
                            groupToWrapper[tree.group] = id

                            addFinalSceneTree(this, tree)
                        }
                    }
                }
            } else {
                addFinalSceneTree(builder, tree)
            }
        }

        fun GLTFBuilder.addFinalSceneTree(builder: GLTFBuilder.Node, tree: MutableGroupTree): Unit = builder.run {

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

                groupToNode[tree.group] = id

                tree.children.forEach {
                    addSceneTree(this, it)
                }

                tree.objects.map { model.getObject(it) }.forEach { addObject(this, it) }
            }
        }

        fun GLTFBuilder.addObject(parent: GLTFBuilder.Node, it: IObject) {
            if (it.ref in animatedObjects) {
                with(parent) {
                    node {
                        name = "${it.name}_animation_wrapper_2"
                        objToWrapper2[it.ref] = id

                        node {
                            name = "${it.name}_animation_wrapper"
                            objToWrapper[it.ref] = id

                            addFinalObject(this, it)
                        }
                    }
                }
            } else {
                addFinalObject(parent, it)
            }
        }


        fun GLTFBuilder.addFinalObject(parent: GLTFBuilder.Node, obj: IObject) = parent.run {
            node {
                name = obj.name

                val objTransform = obj.transformation.toTRS()
                if (objTransform.matrix != Matrix4.IDENTITY) {
                    transformation = GLTFBuilder.Transformation.TRS(
                        objTransform.translation * 0.0625f,
                        objTransform.rotation,
                        objTransform.scale
                    )
                }

                objToNode[obj.ref] = id
                addMesh(this, obj)
            }
        }


        fun GLTFBuilder.addAnimation(anim: IAnimation) {

            val used = anim.channels.values.any { chan ->
                val target = anim.channelMapping[chan.ref] ?: return@any false
                when (target) {
                    is AnimationTargetGroup -> target.ref in groupToNode
                    is AnimationTargetObject -> target.refs.any { it in objToNode }
                }
            }

            if (!used) return

            animation {
                name = anim.name
                anim.channels.values.map { chan ->
                    addAnimationChannel(this, chan, anim)
                }
            }
        }

        fun GLTFBuilder.addAnimationChannel(dsl: GLTFBuilder.Animation, chan: IChannel, anim: IAnimation) = dsl.run {
            val target = anim.channelMapping[chan.ref] ?: return

            val keyframeValues = chan.keyframes.map {
                Animator.combine(TRTSTransformation.IDENTITY, it.value).toTRTS()
            }
            // Avoid duplication of buffers by sharing instances
            val times = buffer(GltfComponentType.FLOAT, chan.keyframes.map { it.time })
            val values = when (chan.type) {
                ChannelType.TRANSLATION -> buffer(GltfComponentType.FLOAT, keyframeValues.map { it.translation * 0.0625 })
                ChannelType.ROTATION -> buffer(GltfComponentType.FLOAT, keyframeValues.map { it.rotation.toQuaternion().toVector4() })
                ChannelType.SCALE -> buffer(GltfComponentType.FLOAT, keyframeValues.map { it.scale })
            }
            val path = when (chan.type) {
                ChannelType.TRANSLATION -> GltfChannelPath.translation
                ChannelType.ROTATION -> GltfChannelPath.rotation
                ChannelType.SCALE -> GltfChannelPath.scale
            }

            when (target) {
                is AnimationTargetGroup -> {

                    if (chan.type == ChannelType.ROTATION) {
                        // translation
                        val pivot = buffer(GltfComponentType.FLOAT, keyframeValues.map { it.pivot * 0.0625 })
                        val negPivot = buffer(GltfComponentType.FLOAT, keyframeValues.map { -it.pivot * 0.0625 })
                        channel {
                            node = groupToWrapper[target.ref]!!
                            interpolation = chan.interpolation.toGLTF()
                            transformType = TRANSLATION
                            timeValues = times
                            transformValues = negPivot
                        }

                        channel {
                            node = groupToWrapper2[target.ref]!!
                            interpolation = chan.interpolation.toGLTF()
                            transformType = ROTATION
                            timeValues = times
                            transformValues = values
                        }

                        channel {
                            node = groupToWrapper2[target.ref]!!
                            interpolation = chan.interpolation.toGLTF()
                            transformType = TRANSLATION
                            timeValues = times
                            transformValues = pivot
                        }
                    } else {
                        channel {
                            node = groupToNode[target.ref]!!
                            interpolation = chan.interpolation.toGLTF()
                            transformType = path
                            timeValues = times
                            transformValues = values
                        }
                    }
                }
                is AnimationTargetObject -> {

                    if (target.refs.isEmpty()) return

                    if (chan.type == ChannelType.ROTATION) {
                        // translation
                        val pivot = buffer(GltfComponentType.FLOAT, keyframeValues.map { it.pivot * 0.0625 })
                        val negPivot = buffer(GltfComponentType.FLOAT, keyframeValues.map { -it.pivot * 0.0625 })
                        target.refs.forEach {

                            channel {
                                node = objToWrapper[it]!!
                                interpolation = chan.interpolation.toGLTF()
                                transformType = TRANSLATION
                                timeValues = times
                                transformValues = negPivot
                            }

                            channel {
                                node = objToWrapper2[it]!!
                                interpolation = chan.interpolation.toGLTF()
                                transformType = ROTATION
                                timeValues = times
                                transformValues = values
                            }

                            channel {
                                node = objToWrapper2[it]!!
                                interpolation = chan.interpolation.toGLTF()
                                transformType = TRANSLATION
                                timeValues = times
                                transformValues = pivot
                            }
                        }
                    } else {
                        target.refs.forEach {
                            channel {
                                node = objToNode[it]!!
                                interpolation = chan.interpolation.toGLTF()
                                transformType = path
                                timeValues = times
                                transformValues = values
                            }
                        }
                    }
                }
            }
        }

        fun InterpolationMethod.toGLTF(): GltfInterpolation = when (this) {
            InterpolationMethod.LINEAR -> GltfInterpolation.LINEAR
            InterpolationMethod.COSINE -> GltfInterpolation.LINEAR
            InterpolationMethod.STEP -> GltfInterpolation.STEP
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
                                    val name = "${prefix}_${material.name}".toLowerCase().removeSuffix(".png").removeSuffix(".png")
                                    var count = 1
                                    var finalName = "$name.png"

                                    while (finalName in pathToImg) { // || File(folder, finalName).exists()) {
                                        finalName = "${name}_$count.png"
                                        count++
                                    }
                                    pathToImg[finalName] = material.path
                                    uri = finalName
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
                        transformationOf(channel.values[index], channel.path).toTRTS()
                    )
                }

                val chan = Channel(
                    name = "Channel",
                    interpolation = InterpolationMethod.LINEAR,
                    keyframes = keyframes
                )

                val thing = nodeMapping[gl.target.node]

                (thing as? IGroupRef)?.let { channelMapping += chan.ref to AnimationTargetGroup(it) }
                (thing as? IObjectRef)?.let { channelMapping += chan.ref to AnimationTargetObject(listOf(it)) }

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



