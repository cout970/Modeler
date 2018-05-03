package com.cout970.modeler.core.export.glTF

import com.cout970.glutilities.memory.expand
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
import org.lwjgl.BufferUtils
import java.nio.ByteBuffer

fun testExporter() = glftModel {

    useExtensions("myExtension")
    requireExtensions("myExtrensionLoader")

    asset {
        copyright = "GNU GPL v3"
    }

    node {
        name = "root"

        transformation {
            translation = Vector3.ZERO
            scale = Vector3.ONE
        }

        node {
            name = "child1"
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
    }
}

fun glftModel(func: GLTFBuilder.() -> Unit): Pair<GLTF.File, ByteArray> {
    val builder = GLTFBuilder()
    builder.func()
    return builder.build()
}

class GLTFBuilder {
    private val extensionsUsed = mutableListOf<String>()
    private val extensionsRequired = mutableListOf<String>()
    private val asset = Asset()

    private val nodes = mutableListOf<Node>()
    private val bakedNodes = mutableListOf<GLTF.Node>()
    private val bakedMeshes = mutableListOf<GLTF.Mesh>()
    private var buffer = BufferUtils.createByteBuffer(16)
    private val bakedBufferViews = mutableListOf<GLTF.BufferView>()
    private val bakedAccessors = mutableListOf<GLTF.Accessor>()
    var bufferName = "model.bin"

    fun useExtensions(vararg extensionsUsed: String) {
        this.extensionsUsed.addAll(extensionsUsed)
    }

    fun requireExtensions(vararg extensionsRequired: String) {
        this.extensionsRequired.addAll(extensionsRequired)
    }

    fun build(): Pair<GLTF.File, ByteArray> {
        val nodes = nodes.build()
        val binary = buffer.toArray()

        return GLTF.File(
                asset = asset.build(),
                nodes = nodes,
                meshes = bakedMeshes,
                bufferViews = bakedBufferViews,
                accessors = bakedAccessors,
                buffers = listOf(GLTF.Buffer(uri = bufferName, byteLength = binary.size))
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
                "generator" to "$NAME gltf Exporter",
                "version" to "2.0"
                // minVersion,
        )

        copyright?.let { map["copyright"] = it }
        return map
    }

    data class Node(
            var transformation: Transformation? = null,
            var name: String? = null,
            var children: MutableList<Node>? = null,
            var mesh: Mesh? = null

//            val camera: Int? = null,                 // The index of the camera referenced by this node.
//            val skin: Int? = null,                 // The index of the skin referenced by this node.

//            val weights: List<Double> = emptyList(),          // The weights of the instantiated Morph Target. Number of elements must match number of Morph Targets of used mesh.
    )

    sealed class Transformation {
        data class Matrix(var matrix: IMatrix4) : Transformation()
        data class TRS(
                var translation: IVector3? = null,
                var rotation: IQuaternion? = null,
                var scale: IVector3? = null
        ) : Transformation()
    }

    fun node(func: Node.() -> Unit) {
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

    fun List<Node>.build(): List<GLTF.Node> {
        bakedNodes.clear()
        forEach { it.build() }
        return bakedNodes
    }

    fun Node.build(): GLTF.Node {
        val bakedChildren = children?.map { it.build() }
        val t = transformation
        val m = mesh?.build()

        val node = GLTF.Node(
                name = name,
                matrix = (t as? Transformation.Matrix)?.matrix,
                translation = (t as? Transformation.TRS)?.translation,
                rotation = (t as? Transformation.TRS)?.rotation,
                scale = (t as? Transformation.TRS)?.scale,
                children = bakedChildren?.map { bakedNodes.indexOf(it) } ?: emptyList(),
                mesh = m?.let { bakedMeshes.indexOf(it) }
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

    fun Mesh.build(): GLTF.Mesh {
        val mesh = GLTF.Mesh(primitives.map { it.build() }, weights, name)
        bakedMeshes.add(mesh)
        return mesh
    }

    data class Primitive(
            val attributes: MutableMap<String, UnpackedBuffer> = mutableMapOf(),
            var indices: UnpackedBuffer? = null,
//            val material: Int? = null,
            var mode: GLTF.GLMode = GLTF.GLMode.TRIANGLES,
            val targets: MutableMap<String, Int> = mutableMapOf()
    ) {

        // this avoid having to import GLTF.ComponentType.*
        inline val BYTE: GLTF.ComponentType get() = GLTF.ComponentType.BYTE
        inline val UNSIGNED_BYTE: GLTF.ComponentType get() = GLTF.ComponentType.UNSIGNED_BYTE
        inline val SHORT: GLTF.ComponentType get() = GLTF.ComponentType.SHORT
        inline val UNSIGNED_SHORT: GLTF.ComponentType get() = GLTF.ComponentType.UNSIGNED_SHORT
        inline val UNSIGNED_INT: GLTF.ComponentType get() = GLTF.ComponentType.UNSIGNED_INT
        inline val FLOAT: GLTF.ComponentType get() = GLTF.ComponentType.FLOAT

        // this avoid having to import GLTF.Attribute.*
        inline val POSITION: String get() = GLTF.Attribute.POSITION.name
        inline val NORMAL: String get() = GLTF.Attribute.NORMAL.name
        inline val TANGEN: String get() = GLTF.Attribute.TANGENT.name
        inline val TEXCOORD_0: String get() = GLTF.Attribute.TEXCOORD_0.name
        inline val TEXCOORD_1: String get() = GLTF.Attribute.TEXCOORD_1.name
        inline val COLOR_0: String get() = GLTF.Attribute.COLOR_0.name
        inline val JOINTS_0: String get() = GLTF.Attribute.JOINTS_0.name
        inline val WEIGHTS_0: String get() = GLTF.Attribute.WEIGHTS_0.name

        // this avoid having to import GLTF.GLMode.*
        inline val POINTS: GLTF.GLMode get() = GLTF.GLMode.POINTS
        inline val LINES: GLTF.GLMode get() = GLTF.GLMode.LINES
        inline val LINE_LOOP: GLTF.GLMode get() = GLTF.GLMode.LINE_LOOP
        inline val LINE_STRIP: GLTF.GLMode get() = GLTF.GLMode.LINE_STRIP
        inline val TRIANGLES: GLTF.GLMode get() = GLTF.GLMode.TRIANGLES
        inline val TRIANGLE_STRIP: GLTF.GLMode get() = GLTF.GLMode.TRIANGLE_STRIP
        inline val TRIANGLE_FAN: GLTF.GLMode get() = GLTF.GLMode.TRIANGLE_FAN
        inline val QUADS: GLTF.GLMode get() = GLTF.GLMode.QUADS
        inline val QUAD_STRIP: GLTF.GLMode get() = GLTF.GLMode.QUAD_STRIP
        inline val POLYGON: GLTF.GLMode get() = GLTF.GLMode.POLYGON
    }

    fun Mesh.primitive(func: Primitive.() -> Unit) {
        primitives.add(Primitive().apply(func))
    }

    inline fun <reified T> Primitive.buffer(type: GLTF.ComponentType, data: List<T>): UnpackedBuffer {
        val container: GLTF.Type = when {
            Number::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.SCALAR
            IVector2::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.VEC2
            IVector3::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.VEC3
            IVector4::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.VEC4
            IMatrix2::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.MAT2
            IMatrix3::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.MAT3
            IMatrix4::class.java.isAssignableFrom(T::class.java) -> GLTF.Type.MAT4
            else -> error("Invalid buffer type")
        }
        return UnpackedBuffer(container, type, data)
    }

    fun Primitive.build(): GLTF.Primitive {
        return GLTF.Primitive(
                attributes = attributes.mapValues { it.value.build() },
                indices = indices?.build(),
                mode = mode.code,
                targets = targets
        )
    }

    data class UnpackedBuffer(
            val containerType: GLTF.Type,
            val elementType: GLTF.ComponentType,
            val data: List<*>
    )

    fun UnpackedBuffer.build(): Int {

        val size = elementType.size * containerType.numComponents * data.size
        val index = bakedBufferViews.size
        val view = GLTF.BufferView(
                buffer = 0,
                name = null,
                byteLength = size,
                byteOffset = buffer.position(),
                byteStride = null,
                target = elementType.id
        )
        val accessor = GLTF.Accessor(
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
                GLTF.ComponentType.BYTE, GLTF.ComponentType.UNSIGNED_BYTE -> buffer.put(n.toByte())
                GLTF.ComponentType.SHORT, GLTF.ComponentType.UNSIGNED_SHORT -> buffer.putShort(n.toShort())
                GLTF.ComponentType.UNSIGNED_INT -> buffer.putInt(n.toInt())
                GLTF.ComponentType.FLOAT -> buffer.putFloat(n.toFloat())
            }
            Unit
        }

        when (containerType) {
            GLTF.Type.SCALAR -> (data as List<Number>).forEach(put)
            GLTF.Type.VEC2 -> (data as List<IVector2>).forEach { put(it.x); put(it.y) }
            GLTF.Type.VEC3 -> (data as List<IVector3>).forEach { put(it.x); put(it.y); put(it.z) }
            GLTF.Type.VEC4 -> (data as List<IVector4>).forEach { put(it.x); put(it.y); put(it.z); put(it.w) }
            GLTF.Type.MAT2, GLTF.Type.MAT3, GLTF.Type.MAT4 -> error("Matrix storage not supported")
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

//    val animations: List<GLTF.Animation> = emptyList(),  // An array of keyframe animations.
//    val cameras: List<GLTF.Camera> = emptyList(),  // An array of cameras. A camera defines a projection matrix.
//    val images: List<GLTF.Image> = emptyList(),  // An array of images. An image defines data used to create a texture.
//    val materials: List<GLTF.Material> = emptyList(),  // An array of materials. A material defines the appearance of a primitive.
//    val samplers: List<GLTF.Sampler> = emptyList(),  // An array of samplers. A sampler contains properties for texture filtering and wrapping modes.
//    val scene: Int? = null,         // The index of the default scene.
//    val scenes: List<GLTF.Scene> = emptyList(),  // An array of scenes.
//    val skins: List<GLTF.Skin> = emptyList(),  // An array of skins. A skin is defined by joints and matrices.
//    val textures: List<GLTF.Texture> = emptyList(),  // An array of textures.
}