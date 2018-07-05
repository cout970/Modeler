package com.cout970.modeler.core.export.glTF

import com.cout970.matrix.api.IMatrix2
import com.cout970.matrix.api.IMatrix3
import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.NAME
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun testExporter() = glftModel {

    useExtensions("myExtension")
    requireExtensions("myExtrensionLoader")

    asset {
        copyright = "GNU GPL v3"
    }

    scene {
        node {
            name = "root"

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

    scene {

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
    private val bakedNodes = mutableListOf<GltfNode>()
    private val bakedMeshes = mutableListOf<GltfMesh>()
    private var buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    private val bakedBufferViews = mutableListOf<GltfBufferView>()
    private val bakedAccessors = mutableListOf<GltfAccessor>()
    var bufferName = "model.bin"

    fun useExtensions(vararg extensionsUsed: String) {
        this.extensionsUsed.addAll(extensionsUsed)
    }

    fun requireExtensions(vararg extensionsRequired: String) {
        this.extensionsRequired.addAll(extensionsRequired)
    }

    fun build(): Pair<GltfFile, ByteArray> {
        val scenes = scenes.build()
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
                materials = bakedMaterials
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
                mesh = m?.let { bakedMeshes.indexOf(it) },
                extras = extras
        )

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

    fun Mesh.build(): GltfMesh {
        val mesh = GltfMesh(primitives.map { it.build() }, weights, name)
        bakedMeshes.add(mesh)
        return mesh
    }

    data class Primitive(
            val attributes: MutableMap<String, UnpackedBuffer> = mutableMapOf(),
            var indices: UnpackedBuffer? = null,
            var material: Material? = null,
            var mode: GltfMode = GltfMode.TRIANGLES,
            val targets: MutableMap<String, Int> = mutableMapOf()
    ) {

        // this avoid having to import ComponentType.*
        inline val BYTE: GltfComponentType get() = GltfComponentType.BYTE
        inline val UNSIGNED_BYTE: GltfComponentType get() = GltfComponentType.UNSIGNED_BYTE
        inline val SHORT: GltfComponentType get() = GltfComponentType.SHORT
        inline val UNSIGNED_SHORT: GltfComponentType get() = GltfComponentType.UNSIGNED_SHORT
        inline val UNSIGNED_INT: GltfComponentType get() = GltfComponentType.UNSIGNED_INT
        inline val FLOAT: GltfComponentType get() = GltfComponentType.FLOAT

        // this avoid having to import Attribute.*
        inline val POSITION: String get() = GltfAttribute.POSITION.name
        inline val NORMAL: String get() = GltfAttribute.NORMAL.name
        inline val TANGEN: String get() = GltfAttribute.TANGENT.name
        inline val TEXCOORD_0: String get() = GltfAttribute.TEXCOORD_0.name
        inline val TEXCOORD_1: String get() = GltfAttribute.TEXCOORD_1.name
        inline val COLOR_0: String get() = GltfAttribute.COLOR_0.name
        inline val JOINTS_0: String get() = GltfAttribute.JOINTS_0.name
        inline val WEIGHTS_0: String get() = GltfAttribute.WEIGHTS_0.name

        // this avoid having to import GLMode.*
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

    inline fun <reified T> Primitive.buffer(type: GltfComponentType, data: List<T>, indices: Boolean = false): UnpackedBuffer {
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

        val mat = material?.build()
        var matIndex: Int? = null
        if (mat != null) {
            matIndex = bakedMaterials.size
            bakedMaterials.add(mat)
        }

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

    fun Material.build(): GltfMaterial {
        return GltfMaterial(
                pbrMetallicRoughness = pbrMetallicRoughness,
                normalTexture = normalTexture,
                occlusionTexture = occlusionTexture,
                emissiveTexture = emissiveTexture,
                emissiveFactor = emissiveFactor,
                alphaMode = alphaMode,
                alphaCutoff = alphaCutoff,
                doubleSided = doubleSided
        )
    }

    data class UnpackedBuffer(
            val containerType: GltfType,
            val elementType: GltfComponentType,
            val data: List<*>,
            val indices: Boolean
    )

    @Suppress("UNCHECKED_CAST")
    fun UnpackedBuffer.build(): Int {

        val size = elementType.size * containerType.numComponents * data.size
        val index = bakedBufferViews.size
        val view = GltfBufferView(
                buffer = 0,
                name = null,
                byteLength = size,
                byteOffset = buffer.position(),
                byteStride = null,
                target = if (indices) 34963 else 34962
        )
        val accessor = GltfAccessor(
                bufferView = index,
                byteOffset = 0,
                componentType = elementType.id,
                normalized = false,
                count = data.size,
                type = containerType,
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
        return index
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
            var pbrMetallicRoughness: GltfPbrMetallicRoughness? = null,
            var normalTexture: GltfNormalTextureInfo? = null,
            var occlusionTexture: GltfOcclusionTextureInfo? = null,
            var emissiveTexture: GltfTextureInfo? = null,
            var emissiveFactor: IVector3 = Vector3.ZERO,
            var alphaMode: GltfAlphaMode = GltfAlphaMode.OPAQUE,
            var alphaCutoff: Double = 0.5,
            var doubleSided: Boolean = false
    )

//    val animations: List<Animation> = emptyList(),  // An array of keyframe animations.
//    val cameras: List<Camera> = emptyList(),  // An array of cameras. A camera defines a projection matrix.
//    val images: List<Image> = emptyList(),  // An array of images. An image defines data used to create a texture.
//    val samplers: List<Sampler> = emptyList(),  // An array of samplers. A sampler contains properties for texture filtering and wrapping modes.
//    val scene: Int? = null,         // The index of the default scene.
//    val scenes: List<Scene> = emptyList(),  // An array of scenes.
//    val skins: List<Skin> = emptyList(),  // An array of skins. A skin is defined by joints and matrices.
//    val textures: List<Texture> = emptyList(),  // An array of textures.
}