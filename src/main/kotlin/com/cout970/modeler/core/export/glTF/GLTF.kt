package com.cout970.modeler.core.export.glTF

import com.cout970.matrix.api.IMatrix4
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.Vector4

typealias JsObject = Map<String, Any>

// https://github.com/KhronosGroup/glTF/blob/master/specification/2.0/README.md#reference-gltf

// @formatter:off
data class GltfFile(
        val extensionsUsed: List<String>        = emptyList(),  // Names of glTF extensions used somewhere in this asset.
        val extensionsRequired: List<String>    = emptyList(),  // Names of glTF extensions required to properly load this asset.
        val accessors: List<GltfAccessor>       = emptyList(),  // An array of accessors. An accessor is a typed view into a bufferView.
        val animations: List<GltfAnimation>     = emptyList(),  // An array of keyframe animations.
        val asset: JsObject                     = emptyMap(),   // Metadata about the glTF asset.
        val buffers: List<GltfBuffer>           = emptyList(),  // An array of buffers. A buffer points to binary geometry, animation, or skins.
        val bufferViews: List<GltfBufferView>   = emptyList(),  // An array of bufferViews. A bufferView is a view into a buffer generally representing a subset of the buffer.
        val cameras: List<GltfCamera>           = emptyList(),  // An array of cameras. A camera defines a projection matrix.
        val images: List<GltfImage>             = emptyList(),  // An array of images. An image defines data used to create a texture.
        val materials: List<GltfMaterial>       = emptyList(),  // An array of materials. A material defines the appearance of a primitive.
        val meshes: List<GltfMesh>              = emptyList(),  // An array of meshes. A mesh is a set of primitives to be rendered.
        val nodes: List<GltfNode>               = emptyList(),  // An array of nodes.
        val samplers: List<GltfSampler>         = emptyList(),  // An array of samplers. A sampler contains properties for texture filtering and wrapping modes.
        val scene: Int?                         = null,         // The index of the default scene.
        val scenes: List<GltfScene>             = emptyList(),  // An array of scenes.
        val skins: List<GltfSkin>               = emptyList(),  // An array of skins. A skin is defined by joints and matrices.
        val textures: List<GltfTexture>         = emptyList(),  // An array of textures.
        val extensions: JsObject?               = null,         // Dictionary object with extension-specific objects.
        val extras: Any?                        = null          // Application-specific data.
){
    override fun toString(): String {
        return "glTF(\n" +
                "  extensionsUsed = $extensionsUsed, \n" +
                "  extensionsRequired = $extensionsRequired, \n" +
                "  accessors = $accessors, \n" +
                "  animations = $animations, \n" +
                "  asset = $asset, \n" +
                "  buffers = $buffers, \n" +
                "  bufferViews = $bufferViews, \n" +
                "  cameras = $cameras, \n" +
                "  images = $images, \n" +
                "  materials = $materials, \n" +
                "  meshes = $meshes, \n" +
                "  nodes = $nodes, \n" +
                "  samplers = $samplers, \n" +
                "  scene = $scene, \n" +
                "  scenes = $scenes, \n" +
                "  skins = $skins, \n" +
                "  textures = $textures, \n" +
                "  extensions = $extensions, \n" +
                "  extras = $extras" +
                "\n)"
    }
}

data class GltfScene(
        val nodes: List<Int>?   = null, // The indices of each root node.
        val name: String?       = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfNode (
        val camera: Int?            = null,                 // The index of the camera referenced by this node.
        val children: List<Int>     = emptyList(),          // The indices of this node's children.
        val skin: Int?              = null,                 // The index of the skin referenced by this node.
        val matrix: IMatrix4? = null,                 // A floating-point 4x4 transformation matrix stored in column-major order.
        val mesh: Int?              = null,                 // The index of the mesh in this node.
        val rotation: IQuaternion?  = Quaternion.IDENTITY,  // The node's unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
        val scale: IVector3?        = Vector3.ONE,          // The node's non-uniform scale, given as the scaling factors along the x, y, and z axes.
        val translation: IVector3?  = Vector3.ZERO,         // The node's translation along the x, y, and z axes.
        val weights: List<Double>   = emptyList(),          // The weights of the instantiated Morph Target. Number of elements must match number of Morph Targets of used mesh.
        val name: String?           = null,                 // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String?     = null,                 // Dictionary object with extension-specific objects.
        val extras: Any?            = null                  // Application-specific data.
)


data class GltfBuffer(
        val uri: String?        = null, // The uri of the buffer. Relative paths are relative to the .gltf file. Instead of referencing an external file, the uri can also be a data-uri.
        val byteLength: Int     = 0,    // The length of the buffer in bytes.
        val name: String?       = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfBufferView(
        val buffer: Int         = 0,    // The index of the buffer.
        val byteOffset: Int?    = 0,    // The offset into the buffer in bytes.
        val byteLength: Int     = 0,    // The length of the bufferView in bytes.
        val byteStride: Int?    = null, // The stride, in bytes, between vertex attributes. When this is not defined, data is tightly packed. When two or more accessors use the same bufferView, this field must be defined.
        val target: Int?        = 0,    // The target that the GPU buffer should be bound to.
        val name: String?       = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfCamera(
        val orthographic: GltfOrthographicCamera?   = null, // An orthographic camera containing properties to create an orthographic projection matrix.
        val perspective: GltfPerspectiveCamera?     = null, // A perspective camera containing properties to create a perspective projection matrix.
        val type: GltfCameraType                    = GltfCameraType.orthographic, // Specifies if the camera uses a perspective or orthographic projection. Based on this, either the camera's perspective or orthographic property will be defined.
        val name: String?                           = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String?                     = null, // Dictionary object with extension-specific objects.
        val extras: Any?                            = null  // Application-specific data.
)

data class GltfAccessor(
        val bufferView: Int?        = 0,           // The index of the bufferView. When not defined, accessor must be initialized with zeros; sparse property or extensions could override zeros with actual values.
        val byteOffset: Int?        = 0,           // The offset relative to the start of the bufferView in bytes. This must be a multiple of the size of the component datatype.
        val componentType: Int      = 0,           // The datatype of components in the attribute. All valid values correspond to WebGL enums. The corresponding typed arrays are Int8Array, Uint8Array, Int16Array, Uint16Array, Uint32Array, and Float32Array, respectively. 5125 (UNSIGNED_INT) is only allowed when the accessor contains indices, i.e., the accessor is only referenced by primitive.indices.
        val normalized: Boolean?    = false,       // Specifies whether integer data values should be normalized (true) to [0, 1] (for unsigned types) or [-1, 1] (for signed types), or converted directly (false) when they are accessed. This property is defined only for accessors that contain vertex attributes or animation output data.
        val count: Int              = 0,           // The number of attributes referenced by this accessor, not to be confused with the number of bytes or number of components.
        val type: GltfType          = GltfType.SCALAR, // Specifies if the attribute is a scalar, vector, or matrix.
        val max: List<Double>       = emptyList(), // Maximum value of each component in this attribute. Array elements must be treated as having the same data type as accessor's componentType. Both min and max arrays have the same length. The length is determined by the value of the type property; it can be 1, 2, 3, 4, 9, or 16.
        val min: List<Double>       = emptyList(), // Minimum value of each component in this attribute. Array elements must be treated as having the same data type as accessor's componentType. Both min and max arrays have the same length. The length is determined by the value of the type property; it can be 1, 2, 3, 4, 9, or 16.
        val sparse: GltfSparse?     = null,        // Sparse storage of attributes that deviate from their initialization value.
        val name: String?           = null,        // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String?     = null,        // Dictionary object with extension-specific objects.
        val extras: Any?            = null         // Application-specific data.
)

data class GltfSparse(
        val count: Int                  = 0,           // The number of attributes encoded in this sparse accessor.
        val indices: List<GltfAccessor> = emptyList(), // Index array of size count that points to those accessor attributes that deviate from their initialization value. Indices must strictly increase.
        val values: List<GltfAccessor>  = emptyList(), // Array of size count times number of components, storing the displaced accessor attributes pointed by indices. Substituted values must have the same componentType and number of components as the base accessor.
        val extensions: String?         = null,        // Dictionary object with extension-specific objects.
        val extras: Any?                = null         // Application-specific data.
)



data class GltfMesh(
        val primitives: List<GltfPrimitive> = emptyList(), // An array of primitives, each defining geometry to be rendered with a material.
        val weights: List<Double>           = emptyList(), // Array of weights to be applied to the Morph Targets.
        val name: String?                   = null,        // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String?             = null,        // Dictionary object with extension-specific objects.
        val extras: Any?                    = null         // Application-specific data.
)

data class GltfPrimitive(
        val attributes: Map<String, Int> = emptyMap(),  // A dictionary object, where each key corresponds to mesh attribute semantic and each value is the index of the accessor containing attribute's data.
        val indices: Int?                = null,        // The index of the accessor that contains mesh indices. When this is not defined, the primitives should be rendered without indices using drawArrays(). When defined, the accessor must contain indices: the bufferView referenced by the accessor should have a target equal to 34963 (ELEMENT_ARRAY_BUFFER); componentType must be 5121 (UNSIGNED_BYTE), 5123 (UNSIGNED_SHORT) or 5125 (UNSIGNED_INT), the latter may require enabling additional hardware support; type must be "SCALAR". For triangle primitives, the front face has a counter-clockwise (CCW) winding order.
        val material: Int?               = null,        // The index of the material to apply to this primitive when rendering.
        val mode: Int                    = 4,           // The type of primitives to render. All valid values correspond to WebGL enums.
        val targets: Map<String, Int>    = emptyMap(),  // An array of Morph Targets, each Morph Target is a dictionary mapping attributes (only POSITION, NORMAL, and TANGENT supported) to their deviations in the Morph Target.
        val extensions: String?          = null,        // Dictionary object with extension-specific objects.
        val extras: Any?                 = null         // Application-specific data.
)

data class GltfSkin(
        val inverseBindMatrices: Int?   = 0,           // The index of the accessor containing the floating-point 4x4 inverse-bind matrices. The default is that each matrix is a 4x4 identity matrix, which implies that inverse-bind matrices were pre-applied.
        val joints: List<Int>           = emptyList(), // The index of the node used as a skeleton root. When undefined, joints transforms resolve to scene root.
        val skeleton: Int?              = 0,           // Indices of skeleton nodes, used as joints in this skin. The array length must be the same as the count property of the inverseBindMatrices accessor (when defined).
        val name: String?               = null,        // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String?         = null,        // Dictionary object with extension-specific objects.
        val extras: Any?                = null         // Application-specific data.
)

data class GltfTexture(
        val sampler: Int?       = 0,    // The index of the sampler used by this texture. When undefined, a sampler with repeat wrapping and auto filtering should be used.
        val source: Int?        = 0,    // The index of the image used by this texture.
        val name: String?       = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfImage(
        val uri: String?        = null, // The uri of the image. Relative paths are relative to the .gltf file. Instead of referencing an external file, the uri can also be a data-uri. The image format must be jpg or png.
        val mimeType: String?   = null, // The image's MIME type.
        val bufferView: Int?    = null, // The index of the bufferView that contains the image. Use this instead of the image's uri property.
        val name: String?       = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)


data class GltfSampler(
        val magFilter: Int?     = null, // Magnification filter. Valid values correspond to WebGL enums: 9728 (NEAREST) and 9729 (LINEAR).
        val minFilter: Int?     = null, // Minification filter. All valid values correspond to WebGL enums.
        val wrapS: Int?         = null, // S (U) wrapping mode. All valid values correspond to WebGL enums.
        val wrapT: Int?         = null, // T (V) wrapping mode. All valid values correspond to WebGL enums.
        val name: String?       = null, // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfMaterial(
        val name: String?                                   = null,             // The user-defined name of this object. This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
        val extensions: String?                             = null,             // Dictionary object with extension-specific objects.
        val extras: Any?                                    = null,             // Application-specific data.
        val pbrMetallicRoughness: GltfPbrMetallicRoughness? = null,             // A set of parameter values that are used to define the metallic-roughness material model from Physically-Based Rendering (PBR) methodology. When not specified, all the default values of pbrMetallicRoughness apply.
        val normalTexture: GltfNormalTextureInfo?           = null,             // A tangent space normal map. The texture contains RGB components in linear space. Each texel represents the XYZ components of a normal vector in tangent space. Red [0 to 255] maps to X [-1 to 1]. Green [0 to 255] maps to Y [-1 to 1]. Blue [128 to 255] maps to Z [1/255 to 1]. The normal vectors use OpenGL conventions where +X is right and +Y is up. +Z points toward the viewer. In GLSL, this vector would be unpacked like so: vec3 normalVector = tex2D(normalMap, texCoord) * 2 - 1. Client implementations should normalize the normal vectors before using them in lighting equations.
        val occlusionTexture: GltfOcclusionTextureInfo?     = null,             // The occlusion map texture. The occlusion values are sampled from the R channel. Higher values indicate areas that should receive full indirect lighting and lower values indicate no indirect lighting. These values are linear. If other channels are present (GBA), they are ignored for occlusion calculations.
        val emissiveTexture: GltfTextureInfo?               = null,             // The emissive map controls the color and intensity of the light being emitted by the material. This texture contains RGB components in sRGB color space. If a fourth component (A) is present, it is ignored.
        val emissiveFactor: IVector3                        = Vector3.ZERO,     // The RGB components of the emissive color of the material. These values are linear. If an emissiveTexture is specified, this value is multiplied with the texel values.
        val alphaMode: GltfAlphaMode                        = GltfAlphaMode.OPAQUE, // The material's alpha rendering mode enumeration specifying the interpretation of the alpha value of the main factor and texture.
        val alphaCutoff: Double                             = 0.5,              // Specifies the cutoff threshold when in MASK mode. If the alpha value is greater than or equal to this value then it is rendered as fully opaque, otherwise, it is rendered as fully transparent. A value greater than 1.0 will render the entire material as fully transparent. This value is ignored for other modes.
        val doubleSided: Boolean                            = false             // Specifies whether the material is double sided. When this value is false, back-face culling is enabled. When this value is true, back-face culling is disabled and double sided lighting is enabled. The back-face must have its normals reversed before the lighting equation is evaluated.
)

data class GltfPbrMetallicRoughness(
        val baseColorFactor: IVector4                   = Vector4.ONE,  // The RGBA components of the base color of the material. The fourth component (A) is the alpha coverage of the material. The alphaMode property specifies how alpha is interpreted. These values are linear. If a baseColorTexture is specified, this value is multiplied with the texel values.
        val baseColorTexture: GltfTextureInfo?          = null,         // The base color texture. This texture contains RGB(A) components in sRGB color space. The first three components (RGB) specify the base color of the material. If the fourth component (A) is present, it represents the alpha coverage of the material. Otherwise, an alpha of 1.0 is assumed. The alphaMode property specifies how alpha is interpreted. The stored texels must not be premultiplied.
        val metallicFactor: Double                      = 1.0,          // The metalness of the material. A value of 1.0 means the material is a metal. A value of 0.0 means the material is a dielectric. Values in between are for blending between metals and dielectrics such as dirty metallic surfaces. This value is linear. If a metallicRoughnessTexture is specified, this value is multiplied with the metallic texel values.
        val roughnessFactor: Double                     = 1.0,          // The roughness of the material. A value of 1.0 means the material is completely rough. A value of 0.0 means the material is completely smooth. This value is linear. If a metallicRoughnessTexture is specified, this value is multiplied with the roughness texel values.
        val metallicRoughnessTexture: GltfTextureInfo?  = null,         // The metallic-roughness texture. The metalness values are sampled from the B channel. The roughness values are sampled from the G channel. These values are linear. If other channels are present (R or A), they are ignored for metallic-roughness calculations.
        val extensions: String?                         = null,         // Dictionary object with extension-specific objects.
        val extras: Any?                                = null          // Application-specific data.
)

data class GltfTextureInfo(
        val index: Int          = 0,    // The index of the texture.
        val texCoord: Int       = 0,    // This integer value is used to construct a string in the format TEXCOORD_ which is a reference to a key in mesh.primitives.attributes (e.g. A value of 0 corresponds to TEXCOORD_0).
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfNormalTextureInfo(
        val index: Int          = 0,    // The index of the texture.
        val texCoord: Int       = 0,    // This integer value is used to construct a string in the format TEXCOORD_ which is a reference to a key in mesh.primitives.attributes (e.g. A value of 0 corresponds to TEXCOORD_0).
        val scale: Double       = 1.0,  // The scalar multiplier applied to each normal vector of the texture. This value scales the normal vector using the formula: scaledNormal = normalize((normalize(<sampled normal texture value>) * 2.0 - 1.0) * vec3(<normal scale>, <normal scale>, 1.0)). This value is ignored if normalTexture is not specified. This value is linear.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfOcclusionTextureInfo(
        val index: Int          = 0,    // The index of the texture.
        val texCoord: Int       = 0,    // This integer value is used to construct a string in the format TEXCOORD_ which is a reference to a key in mesh.primitives.attributes (e.g. A value of 0 corresponds to TEXCOORD_0).
        val strength: Double    = 1.0,  // The scalar multiplier applied to each normal vector of the texture. This value scales the normal vector using the formula: scaledNormal = normalize((normalize(<sampled normal texture value>) * 2.0 - 1.0) * vec3(<normal scale>, <normal scale>, 1.0)). This value is ignored if normalTexture is not specified. This value is linear.
        val extensions: String? = null, // Dictionary object with extension-specific objects.
        val extras: Any?        = null  // Application-specific data.
)

data class GltfPerspectiveCamera(
        val aspectRatio: Double,
        val yfov: Double,
        val znear: Double,
        val zfar: Double = Double.POSITIVE_INFINITY
)

data class GltfOrthographicCamera(
        val xmag: Double,
        val ymag: Double,
        val zfar: Double,
        val znear: Double
)

// @formatter:on

data class GltfAnimation(
        val name: String?,
        val channels: List<GltfAnimationChannel>,
        val samplers: List<GltfAnimationSampler>
)

data class GltfAnimationChannel(
        val sampler: Int,
        val target: GltfChannelTarget
)

data class GltfChannelTarget(
        val node: Int,
        val path: String
)

data class GltfAnimationSampler(
        val input: Int,
        val interpolation: GltfInterpolation,
        val output: Int
)

enum class GltfChannelPath {
    translation, rotation, scale, weights
}

enum class GltfInterpolation {
    LINEAR, STEP, CUBICSPLINE
}

enum class GltfCameraType {
    perspective, orthographic
}

enum class GltfAlphaMode {
    OPAQUE, MASK, BLEND
}

enum class GltfAttribute { // default defined attributes
    POSITION, NORMAL, TANGENT, TEXCOORD_0, TEXCOORD_1, COLOR_0, JOINTS_0, WEIGHTS_0
}

enum class GltfComponentType(val id: Int, val size: Int) {
    BYTE(5120, 1),
    UNSIGNED_BYTE(5121, 1),
    SHORT(5122, 2),
    UNSIGNED_SHORT(5123, 2),
    UNSIGNED_INT(5125, 4),
    FLOAT(5126, 4);

    companion object {
        val conversionMap: Map<Int, GltfComponentType> = values().associateBy { it.id }

        fun fromId(value: Int) = conversionMap[value] ?: error("Invalid Component type value: $value")
    }
}

enum class GltfType(val numComponents: Int) {
    SCALAR(1),
    VEC2(2),
    VEC3(3),
    VEC4(4),
    MAT2(4),
    MAT3(9),
    MAT4(16)
}

enum class GltfMode(val code: Int) {
    POINTS(0x0),
    LINES(0x1),
    LINE_LOOP(0x2),
    LINE_STRIP(0x3),
    TRIANGLES(0x4),
    TRIANGLE_STRIP(0x5),
    TRIANGLE_FAN(0x6),
    QUADS(0x7),
    QUAD_STRIP(0x8),
    POLYGON(0x9);

    companion object {
        val conversionMap: Map<Int, GltfMode> = GltfMode.values().associateBy { it.code }

        fun fromId(value: Int) = conversionMap[value] ?: error("Invalid GL mode: $value")
    }
}