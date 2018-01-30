package com.cout970.modeler.core.export

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import com.google.gson.GsonBuilder
import java.io.File


object GlTFHandler {

    private val GSON = GsonBuilder().create()

    fun export(file: File, model: GlFT.File) = file.writeText(GSON.toJson(model))

    fun import(file: File) = GSON.fromJson(file.readText(), GlFT.File::class.java)
}

object GlFT {

    data class File(
            val asset: Map<String, String>,
            val scenes: List<Scene>,
            val scene: Int?,
            val nodes: List<Node>,
            val buffers: List<Buffer>,
            val bufferViews: List<BufferView>,
            val accessors: List<Accessor>,
            val meshes: List<Mesh>,
            val skins: List<Skin>,
            val textures: List<Texture>,
            val images: List<Image>,
            val samplers: List<Sampler>,
            val materials: List<Material>,
            val cameras: List<Camera>,
            val animations: List<Animation>
    )

    data class Scene(
            val name: String,
            val nodes: List<Int>
    )

    data class Node(
            val name: String?,
            val mesh: Int?,
            val children: List<Int>,
            val rotation: IQuaternion = Quaternion.IDENTITY,
            val translation: IVector3 = Vector3.ZERO,
            val scale: IVector3 = Vector3.ONE,
            val matrix: IMatrix4 = Matrix4.IDENTITY,
            val camera: Int?,
            val weights: List<Double>,
            val skin: Int?
    )

    data class Buffer(
            val byteLength: Int,
            val uri: String? // GLB-stored BIN chunk, must have buffer.uri property undefined
    )

    data class BufferView(
            val buffer: Int,
            val byteLength: Int,
            val byteOffset: Int,
            val byteStride: Int?,
            val target: Int?
    )

    data class Accessor(
            val bufferView: Int?,
            val byteOffset: Int,
            val componentType: Int,
            val count: Int,         // number of elements, not bytes
            val max: List<Number>, // Exporters and loaders must treat these values as
            val min: List<Number>, // having the same data type as accessor's componentType
            val type: Type
    )

    data class Sparse(
            val count: Int,
            val indices: List<Accessor>,
            val values: List<Accessor>
    )

    data class Mesh(
            val name: String?,
            val primitives: List<Primitive>,
            val weights: List<Double>
    )

    data class Primitive(
            val attributes: Map<String, Int>,
            val indices: Int,
            val material: Int?,
            val mode: Int,
            val targets: Map<String, Int>
    )

    data class Skin(
            val inverseBindMatrices: Int,
            val joints: List<Int>,
            val skeleton: Int
    )

    data class Texture(
            val sampler: Int,
            val source: Int
    )

    data class Image(
            val uri: String?,
            val bufferView: Int?,
            val mimeType: String?
    )

    data class Sampler(
            val magFilter: Int?,
            val minFilter: Int?,
            val wrapS: Int?,
            val wrapT: Int?
    )

    data class Material(
            val name: String,
            val pbrMetallicRoughness: PbrMetallicRoughness,
            val normalTexture: NormalTexture,
            val emissiveFactor: IVector3,
            val alphaMode: AlphaMode = AlphaMode.OPAQUE,
            val doubleSided: Boolean
    )

    data class PbrMetallicRoughness(
            val baseColorFactor: IVector4,
            val metallicFactor: Double,
            val roughnessFactor: Double
    )

    data class NormalTexture(
            val scale: Double,
            val index: Int,
            val texCoord: Int
    )

    data class Camera(
            val name: String,
            val type: CameraType,
            val perspective: PerspectiveCamera?,
            val orthographic: OrthographicCamera?
    )

    data class PerspectiveCamera(
            val aspectRatio: Double,
            val yfov: Double,
            val znear: Double,
            val zfar: Double = Double.POSITIVE_INFINITY
    )

    data class OrthographicCamera(
            val xmag: Double,
            val ymag: Double,
            val zfar: Double,
            val znear: Double
    )

    data class Animation(
            val name: String,
            val channels: List<AnimationChannel>,
            val samplers: List<AnimationSampler>
    )

    data class AnimationChannel(
            val sampler: Int,
            val target: ChannelTarget
    )

    data class ChannelTarget(
            val node: Int,
            val path: String
    )

    data class AnimationSampler(
            val input: Int,
            val interpolation: Interpolation,
            val output: Int
    )

    enum class ChannelPath {
        translation, rotation, scale, weights
    }

    enum class Interpolation{
        LINEAR, STEP, CUBICSPLINE
    }

    enum class CameraType {
        perspective, orthographic
    }

    enum class AlphaMode {
        OPAQUE, MASK, BLEND
    }

    enum class Attribute { // default defined attributes
        POSITION, NORMAL, TANGENT, TEXCOORD_0, TEXCOORD_1, COLOR_0, JOINTS_0, WEIGHTS_0
    }

    enum class ComponentType(val id: Int, val size: Int) {
        BYTE(5120, 1),
        UNSIGNED_BYTE(5121, 1),
        SHORT(5122, 2),
        UNSIGNED_SHORT(5123, 2),
        UNSIGNED_INT(5125, 4),
        FLOAT(5126, 4);

        val conversionMap: Map<Int, ComponentType> = values().associateBy { it.id }
    }

    enum class Type(val numComponents: Int) {
        SCALAR(1),
        VEC2(2),
        VEC3(3),
        VEC4(4),
        MAT2(4),
        MAT3(9),
        MAT4(16)
    }
}
