package com.cout970.modeler.core.export.glTF

import com.cout970.glutilities.texture.Texture
import com.cout970.matrix.api.IMatrix2
import com.cout970.matrix.api.IMatrix3
import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.NAME
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.Vector4
import com.cout970.vector.extensions.vec3Of
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

fun testExporter() = glftModel {

    useExtensions("myExtension")
    requireExtensions("myExtrensionLoader")

    asset {
        copyright = "GNU GPL v3"
    }

    var rootNodeId: UUID? = null

    scene {
        node {
            name = "root"
            rootNodeId = id

            transformation {
                translation = Vector3.ZERO
                scale = Vector3.ONE
            }

            node {
                name = "child1"
                cubeMesh(this)
            }
        }
    }

    animation {
        name = "Translation to (1,1,1)"
        channel {
            node = rootNodeId
            interpolation = LINEAR
            transformType = TRANSLATION
            timeValues = buffer(FLOAT, listOf(1f, 2f, 3f, 4f))
            transformValues = buffer(FLOAT, listOf(vec3Of(0), vec3Of(1), vec3Of(1), vec3Of(0)))
        }
    }
}

private fun GLTFBuilder.cubeMesh(node: GLTFBuilder.Node) = node.apply {
    mesh {
        name = "Cube"

        primitive {
            mode = TRIANGLES

            attributes[POSITION] = buffer(FLOAT, listOf(
                    vec3Of(-1.0f, 1.0f, 1.0f),
                    vec3Of(1.0f, 1.0f, 1.0f),
                    vec3Of(-1.0f, -1.0f, 1.0f),
                    vec3Of(1.0f, -1.0f, 1.0f),
                    vec3Of(-1.0f, 1.0f, -1.0f),
                    vec3Of(1.0f, 1.0f, -1.0f),
                    vec3Of(-1.0f, -1.0f, -1.0f),
                    vec3Of(1.0f, -1.0f, -1.0f)
            ))

            indices = buffer(UNSIGNED_INT, listOf(
                    0, 1, 2, // 0
                    1, 3, 2,
                    4, 6, 5, // 2
                    5, 6, 7,
                    0, 2, 4, // 4
                    4, 2, 6,
                    1, 5, 3, // 6
                    5, 7, 3,
                    0, 4, 1, // 8
                    4, 5, 1,
                    2, 3, 6, // 10
                    6, 3, 7
            ))
        }
    }
}

fun glftModel(func: GLTFBuilder.() -> Unit): Pair<GltfFile, ByteArray> {
    val builder = GLTFBuilder()
    builder.func()
    return builder.build()
}

fun main(args: Array<String>) {
    println(testExporter().first)
}

class GLTFBuilder {
    private val extensionsUsed = mutableListOf<String>()
    private val extensionsRequired = mutableListOf<String>()
    private val asset = Asset()

    private val scenes = mutableListOf<Scene>()
    private val bakedMaterials = mutableListOf<GltfMaterial>()
    private val materialsMap = mutableMapOf<UUID, Int>()
    private val bakedImages = mutableListOf<GltfImage>()
    private val bakedSamplers = mutableListOf<GltfSampler>()
    private val bakedTextures = mutableListOf<GltfTexture>()
    private val bakedNodes = mutableListOf<GltfNode>()
    private val nodeIdToIndex = mutableMapOf<UUID, Int>()
    private val bakedMeshes = mutableListOf<GltfMesh>()
    private var buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    private val bakedBufferViews = mutableListOf<GltfBufferView>()
    private val bakedAccessors = mutableListOf<GltfAccessor>()
    private val animations = mutableListOf<Animation>()
    private val meshToId = mutableMapOf<Mesh, Int>()
    private val bufferToId = mutableMapOf<UnpackedBuffer, Int>()


    var bufferName = "model.bin"

    fun useExtensions(vararg extensionsUsed: String) {
        this.extensionsUsed.addAll(extensionsUsed)
    }

    fun requireExtensions(vararg extensionsRequired: String) {
        this.extensionsRequired.addAll(extensionsRequired)
    }

    fun build(): Pair<GltfFile, ByteArray> {
        val scenes = scenes.build()
        val animations = animations.map { it.build() }
        val binary = buffer.toArray()

        return GltfFile(
                asset = asset.build(),
                nodes = bakedNodes,
                meshes = bakedMeshes,
                bufferViews = bakedBufferViews,
                accessors = bakedAccessors,
                scene = 0,
                scenes = scenes,
                buffers = listOf(GltfBuffer(uri = bufferName, byteLength = binary.size)),
                materials = bakedMaterials,
                animations = animations,
                images = bakedImages,
                textures = bakedTextures,
                samplers = bakedSamplers
        ) to binary
    }

    data class Asset(
            var copyright: String? = null
    )

    fun asset(func: Asset.() -> Unit) {
        this.asset.func()
    }

    fun Asset.build(): JsObject {
        val map = mutableMapOf(
                "generator" to "$NAME glTF v2 Exporter",
                "version" to "2.0"
        )

        copyright?.let { map["copyright"] = it }
        return map
    }

    data class Scene(
            val nodes: MutableList<Node> = mutableListOf(),
            var name: String? = null,
            var extras: Any? = null
    )

    fun scene(func: Scene.() -> Unit) {
        scenes.add(Scene().apply(func))
    }

    @JvmName("buildScenes")
    private fun List<Scene>.build(): List<GltfScene> {
        return map { it.build() }
    }

    fun Scene.build(): GltfScene {
        val indices = nodes.map {
            it.build()
            bakedNodes.size - 1
        }

        return GltfScene(
                nodes = indices,
                name = name,
                extras = extras
        )
    }

    data class Node(
            val id: UUID = UUID.randomUUID(),
            var transformation: Transformation? = null,
            var name: String? = null,
            var children: MutableList<Node>? = null,
            var mesh: Mesh? = null,
            var extras: Any? = null
    )

    sealed class Transformation {
        data class Matrix(var matrix: IMatrix4) : Transformation()
        data class TRS(
                var translation: IVector3? = null,
                var rotation: IQuaternion? = null,
                var scale: IVector3? = null
        ) : Transformation()
    }

    fun Scene.node(func: Node.() -> Unit) {
        nodes.add(Node().apply(func))
    }

    fun Node.node(func: Node.() -> Unit) {
        val list = children ?: mutableListOf()
        list.add(Node().apply(func))
        children = list
    }

    fun Node.transformation(mat: IMatrix4) {
        transformation = Transformation.Matrix(mat)
    }

    fun Node.transformation(func: Transformation.TRS.() -> Unit) {
        transformation = Transformation.TRS().apply(func)
    }

    @JvmName("buildNodes")
    private fun List<Node>.build(): List<GltfNode> {
        bakedNodes.clear()
        forEach { it.build() }
        return bakedNodes
    }

    fun Node.build(): GltfNode {
        val bakedChildren = children?.map { it.build() }
        val t = transformation
        val m = mesh?.build()

        val node = GltfNode(
                name = name,
                matrix = (t as? Transformation.Matrix)?.matrix,
                translation = (t as? Transformation.TRS)?.translation,
                rotation = (t as? Transformation.TRS)?.rotation,
                scale = (t as? Transformation.TRS)?.scale,
                children = bakedChildren?.map { bakedNodes.indexOf(it) } ?: emptyList(),
                mesh = m,
                extras = extras
        )

        nodeIdToIndex[id] = bakedNodes.size
        bakedNodes.add(node)
        return node
    }

    data class Mesh(
            var name: String? = null,
            var primitives: MutableList<Primitive> = mutableListOf(),
            var weights: MutableList<Double> = mutableListOf()
    )

    fun Node.mesh(func: Mesh.() -> Unit) {
        mesh = Mesh().apply(func)
    }

    fun Mesh.build(): Int? {
        if (this in meshToId) {
            return meshToId[this]
        }
        val mesh = GltfMesh(primitives.map { it.build() }, weights, name)
        bakedMeshes.add(mesh)
        meshToId[this] = bakedMeshes.size - 1
        return bakedMeshes.size - 1
    }

    @Suppress("PropertyName", "unused")
    data class Primitive(
            val attributes: MutableMap<String, UnpackedBuffer> = mutableMapOf(),
            var indices: UnpackedBuffer? = null,
            var material: Material? = null,
            var mode: GltfMode = GltfMode.TRIANGLES,
            val targets: MutableMap<String, Int> = mutableMapOf()
    ) {

        // this avoids having to import ComponentType.*
        inline val BYTE: GltfComponentType get() = GltfComponentType.BYTE
        inline val UNSIGNED_BYTE: GltfComponentType get() = GltfComponentType.UNSIGNED_BYTE
        inline val SHORT: GltfComponentType get() = GltfComponentType.SHORT
        inline val UNSIGNED_SHORT: GltfComponentType get() = GltfComponentType.UNSIGNED_SHORT
        inline val UNSIGNED_INT: GltfComponentType get() = GltfComponentType.UNSIGNED_INT
        inline val FLOAT: GltfComponentType get() = GltfComponentType.FLOAT

        // this avoids having to import Attribute.*
        inline val POSITION: String get() = GltfAttribute.POSITION.name
        inline val NORMAL: String get() = GltfAttribute.NORMAL.name
        inline val TANGEN: String get() = GltfAttribute.TANGENT.name
        inline val TEXCOORD_0: String get() = GltfAttribute.TEXCOORD_0.name
        inline val TEXCOORD_1: String get() = GltfAttribute.TEXCOORD_1.name
        inline val COLOR_0: String get() = GltfAttribute.COLOR_0.name
        inline val JOINTS_0: String get() = GltfAttribute.JOINTS_0.name
        inline val WEIGHTS_0: String get() = GltfAttribute.WEIGHTS_0.name

        // this avoids having to import GLMode.*
        inline val POINTS: GltfMode get() = GltfMode.POINTS
        inline val LINES: GltfMode get() = GltfMode.LINES
        inline val LINE_LOOP: GltfMode get() = GltfMode.LINE_LOOP
        inline val LINE_STRIP: GltfMode get() = GltfMode.LINE_STRIP
        inline val TRIANGLES: GltfMode get() = GltfMode.TRIANGLES
        inline val TRIANGLE_STRIP: GltfMode get() = GltfMode.TRIANGLE_STRIP
        inline val TRIANGLE_FAN: GltfMode get() = GltfMode.TRIANGLE_FAN
        inline val QUADS: GltfMode get() = GltfMode.QUADS
        inline val QUAD_STRIP: GltfMode get() = GltfMode.QUAD_STRIP
        inline val POLYGON: GltfMode get() = GltfMode.POLYGON
    }

    fun Mesh.primitive(func: Primitive.() -> Unit) {
        primitives.add(Primitive().apply(func))
    }

    inline fun <reified T> buffer(type: GltfComponentType, data: List<T>, indices: Boolean = false): UnpackedBuffer {
        val container: GltfType = when {
            Number::class.java.isAssignableFrom(T::class.java) -> GltfType.SCALAR
            IVector2::class.java.isAssignableFrom(T::class.java) -> GltfType.VEC2
            IVector3::class.java.isAssignableFrom(T::class.java) -> GltfType.VEC3
            IVector4::class.java.isAssignableFrom(T::class.java) -> GltfType.VEC4
            IMatrix2::class.java.isAssignableFrom(T::class.java) -> GltfType.MAT2
            IMatrix3::class.java.isAssignableFrom(T::class.java) -> GltfType.MAT3
            IMatrix4::class.java.isAssignableFrom(T::class.java) -> GltfType.MAT4
            else -> error("Invalid buffer type")
        }
        return UnpackedBuffer(container, type, data, indices)
    }

    fun Primitive.build(): GltfPrimitive {
        val matIndex: Int? = material?.build()

        return GltfPrimitive(
                attributes = attributes.mapValues { it.value.build() },
                indices = indices?.build(),
                material = matIndex,
                mode = mode.code,
                targets = targets
        )
    }

    fun Primitive.material(func: Material.() -> Unit) {
        material = Material().apply(func)
    }

    fun Material.build(): Int {

        require(id != null) { "Material doesn't have ID" }

        if (id in materialsMap) {
            return materialsMap[id]!!
        }

        materialsMap[id!!] = bakedMaterials.size
        bakedMaterials.add(GltfMaterial(
                pbrMetallicRoughness = pbrMetallicRoughness?.build(),
                normalTexture = normalTexture,
                occlusionTexture = occlusionTexture,
                emissiveTexture = emissiveTexture,
                emissiveFactor = emissiveFactor,
                alphaMode = alphaMode ?: GltfAlphaMode.MASK,
                alphaCutoff = alphaCutoff ?: 0.5,
                doubleSided = doubleSided
        ))

        return bakedMaterials.size - 1
    }

    data class UnpackedBuffer(
            val containerType: GltfType,
            val elementType: GltfComponentType,
            val data: List<*>,
            val indices: Boolean
    ) {
        init {
            require(data.isNotEmpty()) { "Unable to build an empty Buffer" }

            val size = elementType.size * containerType.numComponents * data.size
            require(size != 0) { "Unable to build a Buffer of size 0" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun UnpackedBuffer.build(animation: Boolean = false): Int {

        if (this in bufferToId) {
            return bufferToId[this]!!
        }

        val size = elementType.size * containerType.numComponents * data.size
        val index = bakedBufferViews.size
        val view = GltfBufferView(
                buffer = 0,
                name = null,
                byteLength = size,
                byteOffset = buffer.position(),
                byteStride = null,
                target = if (animation) null else if (indices) 34963 else 34962
        )
        val accessor = GltfAccessor(
                bufferView = index,
                byteOffset = 0,
                componentType = elementType.id,
                normalized = false,
                count = data.size,
                type = containerType,
                min = getMin(this),
                max = getMax(this),
                name = null
        )

        val put = { n: Number ->

            if (buffer.capacity() < buffer.position() + 16 * 4) {
                buffer = buffer.expand(buffer.capacity() * 2)
            }

            when (elementType) {
                GltfComponentType.BYTE, GltfComponentType.UNSIGNED_BYTE -> buffer.put(n.toByte())
                GltfComponentType.SHORT, GltfComponentType.UNSIGNED_SHORT -> buffer.putShort(n.toShort())
                GltfComponentType.UNSIGNED_INT -> buffer.putInt(n.toInt())
                GltfComponentType.FLOAT -> buffer.putFloat(n.toFloat())
            }
            Unit
        }

        when (containerType) {
            GltfType.SCALAR -> (data as List<Number>).forEach(put)
            GltfType.VEC2 -> (data as List<IVector2>).forEach { put(it.x); put(it.y) }
            GltfType.VEC3 -> (data as List<IVector3>).forEach { put(it.x); put(it.y); put(it.z) }
            GltfType.VEC4 -> (data as List<IVector4>).forEach { put(it.x); put(it.y); put(it.z); put(it.w) }
            GltfType.MAT2, GltfType.MAT3, GltfType.MAT4 -> error("Matrix storage not supported")
        }

        bakedBufferViews.add(view)
        bakedAccessors.add(accessor)
        bufferToId[this] = index
        return index
    }

    private fun getMin(buff: UnpackedBuffer): List<Double> {
        val numbers = mutableListOf<List<Double>>()
        when (buff.containerType) {
            GltfType.SCALAR -> {
                numbers += buff.data.map { (it as Number).toDouble() }
            }
            GltfType.VEC2 -> {
                val vecs = buff.data.map { it as IVector2 }
                numbers += vecs.map { it.xd }
                numbers += vecs.map { it.yd }
            }
            GltfType.VEC3 -> {
                val vecs = buff.data.map { it as IVector3 }
                numbers += vecs.map { it.xd }
                numbers += vecs.map { it.yd }
                numbers += vecs.map { it.zd }
            }
            GltfType.VEC4 -> {
                val vecs = buff.data.map { it as IVector4 }
                numbers += vecs.map { it.xd }
                numbers += vecs.map { it.yd }
                numbers += vecs.map { it.zd }
                numbers += vecs.map { it.wd }
            }
            GltfType.MAT2, GltfType.MAT3, GltfType.MAT4 -> error("Not supported")
        }
        return numbers.map { it.min() ?: 0.0 }
    }

    private fun getMax(buff: UnpackedBuffer): List<Double> {
        val numbers = mutableListOf<List<Double>>()
        when (buff.containerType) {
            GltfType.SCALAR -> {
                numbers += buff.data.map { (it as Number).toDouble() }
            }
            GltfType.VEC2 -> {
                val vecs = buff.data.map { it as IVector2 }
                numbers += vecs.map { it.xd }
                numbers += vecs.map { it.yd }
            }
            GltfType.VEC3 -> {
                val vecs = buff.data.map { it as IVector3 }
                numbers += vecs.map { it.xd }
                numbers += vecs.map { it.yd }
                numbers += vecs.map { it.zd }
            }
            GltfType.VEC4 -> {
                val vecs = buff.data.map { it as IVector4 }
                numbers += vecs.map { it.xd }
                numbers += vecs.map { it.yd }
                numbers += vecs.map { it.zd }
                numbers += vecs.map { it.wd }
            }
            GltfType.MAT2, GltfType.MAT3, GltfType.MAT4 -> error("Not supported")
        }
        return numbers.map { it.max() ?: 0.0 }
    }

    fun ByteBuffer.toArray(): ByteArray {
        val array = ByteArray(position())
        flip()
        repeat(array.size) {
            array[it] = get()
        }
        return array
    }

    fun ByteBuffer.expand(newSize: Int): ByteBuffer {
        this.flip()
        return ByteBuffer.allocate(newSize).put(this).order(ByteOrder.LITTLE_ENDIAN)
    }

    data class Material(
            var id: UUID? = null,
            var pbrMetallicRoughness: PbrMetallicRoughness? = null,
            var normalTexture: GltfNormalTextureInfo? = null,
            var occlusionTexture: GltfOcclusionTextureInfo? = null,
            var emissiveTexture: GltfTextureInfo? = null,
            var emissiveFactor: IVector3? = null,
            var alphaMode: GltfAlphaMode? = null,
            var alphaCutoff: Double? = null,
            var doubleSided: Boolean = false
    )

    data class PbrMetallicRoughness(
            var baseColorFactor: IVector4? = null,
            var baseColorTexture: TextureInfo? = null,
            var metallicFactor: Double? = null,
            var roughnessFactor: Double? = null,
            var metallicRoughnessTexture: TextureInfo? = null
    )

    data class TextureInfo(
            var image: Image? = null,
            var texCoord: Int = 0
    )

    data class Image(
            var uri: String? = null,
            var mimeType: String? = null,
            var name: String? = null
    )

    fun Material.pbrMetallicRoughness(func: PbrMetallicRoughness.() -> Unit) {
        val data = PbrMetallicRoughness()
        func(data)
        pbrMetallicRoughness = data
    }

    fun PbrMetallicRoughness.baseColor(func: Image.() -> Unit) {
        baseColorTexture = TextureInfo(Image().apply(func))
    }

    fun PbrMetallicRoughness.build(): GltfPbrMetallicRoughness {
        return GltfPbrMetallicRoughness(
                baseColorFactor = baseColorFactor ?: Vector4.ONE,
                baseColorTexture = baseColorTexture?.build(),
                metallicFactor = metallicFactor ?: 1.0,
                roughnessFactor = roughnessFactor ?: 1.0,
                metallicRoughnessTexture = metallicRoughnessTexture?.build()
        )
    }

    fun TextureInfo.build(): GltfTextureInfo {
        return GltfTextureInfo(
                index = image!!.build(),
                texCoord = texCoord
        )
    }

    fun Image.build(): Int {
        if (bakedSamplers.isEmpty()) {
            bakedSamplers.add(GltfSampler(
                    magFilter = Texture.PIXELATED,
                    minFilter = Texture.PIXELATED,
                    wrapS = Texture.REPEAT,
                    wrapT = Texture.REPEAT,
                    name = "pixelated"
            ))
        }

        bakedImages.add(GltfImage(
                uri = uri
        ))

        bakedTextures.add(GltfTexture(
                sampler = 0,
                source = bakedImages.size - 1,
                name = name
        ))

        return bakedTextures.size - 1
    }

    fun animation(func: Animation.() -> Unit) {
        val anim = Animation()
        anim.func()
        animations.add(anim)
    }

    fun Animation.channel(func: Channel.() -> Unit) {
        val chan = Channel()
        chan.func()
        channels.add(chan)
    }

    private fun Animation.build(): GltfAnimation {
        val channels = mutableListOf<GltfAnimationChannel>()
        val samplers = mutableListOf<GltfAnimationSampler>()

        this.channels.forEach { chan ->
            val node = nodeIdToIndex[chan.node] ?: return@forEach

            channels += GltfAnimationChannel(
                    target = GltfChannelTarget(node = node, path = chan.transformType.toString()),
                    sampler = samplers.size
            )

            samplers += GltfAnimationSampler(
                    input = chan.timeValues!!.build(true),
                    interpolation = chan.interpolation,
                    output = chan.transformValues!!.build(true)
            )
        }

        return GltfAnimation(
                name = name,
                channels = channels,
                samplers = samplers
        )
    }

    data class Animation(
            var name: String? = null,
            val channels: MutableList<Channel> = mutableListOf()
    )

    data class Channel(
            var node: UUID? = null,
            var interpolation: GltfInterpolation = GltfInterpolation.LINEAR,
            var transformType: GltfChannelPath = GltfChannelPath.translation,
            var timeValues: UnpackedBuffer? = null,
            var transformValues: UnpackedBuffer? = null
    ) {

        // this avoids having to import GltfInterpolation.*
        inline val LINEAR: GltfInterpolation get() = GltfInterpolation.LINEAR
        inline val STEP: GltfInterpolation get() = GltfInterpolation.STEP
        inline val CUBICSPLINE: GltfInterpolation get() = GltfInterpolation.CUBICSPLINE

        // this avoids having to import GltfChannelPath.*
        inline val TRANSLATION: GltfChannelPath get() = GltfChannelPath.translation
        inline val ROTATION: GltfChannelPath get() = GltfChannelPath.rotation
        inline val SCALE: GltfChannelPath get() = GltfChannelPath.scale
        inline val WEIGHTS: GltfChannelPath get() = GltfChannelPath.weights

        // this avoids having to import ComponentType.*
        inline val BYTE: GltfComponentType get() = GltfComponentType.BYTE
        inline val UNSIGNED_BYTE: GltfComponentType get() = GltfComponentType.UNSIGNED_BYTE
        inline val SHORT: GltfComponentType get() = GltfComponentType.SHORT
        inline val UNSIGNED_SHORT: GltfComponentType get() = GltfComponentType.UNSIGNED_SHORT
        inline val UNSIGNED_INT: GltfComponentType get() = GltfComponentType.UNSIGNED_INT
        inline val FLOAT: GltfComponentType get() = GltfComponentType.FLOAT
    }

//    val cameras: List<Camera> = emptyList(),  // An array of cameras. A camera defines a projection matrix.
//    val images: List<Image> = emptyList(),  // An array of images. An image defines data used to create a texture.
//    val samplers: List<Sampler> = emptyList(),  // An array of samplers. A sampler contains properties for texture filtering and wrapping modes.
//    val skins: List<Skin> = emptyList(),  // An array of skins. A skin is defined by joints and matrices.
//    val textures: List<Texture> = emptyList(),  // An array of textures.
}