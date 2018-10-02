package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.GroupNone
import com.cout970.modeler.api.model.`object`.MutableGroupTree
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.`object`.ObjectNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.core.model.toMutable
import com.cout970.modeler.core.model.toTRS
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.input.dialogs.MessageDialogs
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec4Of
import com.google.gson.GsonBuilder
import java.io.File
import java.io.IOException

private val gson = GsonBuilder()
    .registerTypeAdapter(IVector4::class.java, Vector4Serializer())
    .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
    .registerTypeAdapter(List::class.java, EmptyListAdapter)
    .registerTypeAdapter(Map::class.java, EmptyMapAdapter)
    .setPrettyPrinting()
    .create()!!


class VsImporter {

    fun import(path: ResourcePath): IModel {
        TODO()
    }
}

class VsExporter {

    private val cubeMesh = MeshFactory.createCube(Vector3.ONE, Vector3.ORIGIN)
    private val faceOrder = listOf(
        VsEnumSide.down,
        VsEnumSide.up,
        VsEnumSide.north,
        VsEnumSide.south,
        VsEnumSide.west,
        VsEnumSide.east
    )

    fun export(file: File, model: IModel) {
        val warnings = mutableListOf<String>()
        val (vsModel, images) = model.toVsModel(warnings)

        val allowed = warnings.isEmpty() || MessageDialogs.warningBoolean(
            title = "Export warnings",
            message = "${warnings.joinToString("\n")}\nDo you want to continue?",
            default = true
        )

        if (allowed) {
            file.writeText(gson.toJson(vsModel))

            images.forEach { (name, path) ->
                try {
                    File(file.parentFile, "$name.png").writeBytes(path.inputStream().readBytes())
                } catch (e: IOException) {
                    e.print()
                }
            }
        }
    }

    private fun IModel.toVsModel(warnings: MutableList<String>): Pair<VsModel, Map<String, ResourcePath>> {

        val root = tree.toMutable()
        val materials = materialMap.values
            .filterIsInstance<TexturedMaterial>()

        val images = materials
            .map { texturePath(it.path) to it.path }
            .toMap()

        val texture = materials
            .map { textureName(it.name) to texturePath(it.path) }
            .toMap()

        val first = materials.firstOrNull()

        materials.filter { it.size != first!!.size }.forEach {
            warnings += "Texture size differs from the first texture, expected: ${first!!.size}, found: ${it.size}, in ${it.name} (${it.path})"
        }

        val textureScale = first?.size ?: vec2Of(32)

        val elements = root.objects.mapNotNull { it.toElement(this, textureScale, warnings) } +
            root.children.mapNotNull { it.toElement(this, textureScale, warnings) }

        return VsModel(
            editor = mapOf("allAngles" to false, "singleTexture" to (texture.size == 1)),
            textureWidth = textureScale.xi,
            textureHeight = textureScale.yi,
            textures = texture,
            elements = elements,
            ambientocclusion = null,
            animations = emptyList()
        ) to images
    }

    private fun IObjectRef.toElement(model: IModel, textureScale: IVector2, warnings: MutableList<String>): VsElement? {
        val obj = model.getObject(this)
        if (obj == ObjectNone) return null

        if (obj is Object && obj.mesh.pos != cubeMesh.pos) {
            // not a cube
            warnings += "Unable to export cube: ${obj.name}"
            return null
        }

        val trs = obj.transformation.toTRS()

        val from = trs.translation
        val to = trs.translation + trs.scale

        val rot = trs.euler.angles.let { if (it == Vector3.ZERO) null else it }

        val material = model.materialMap[obj.material]
        val texture = textureName(material?.name)

        val mesh = obj.mesh
        val faces = mesh.faces.mapIndexed { index, face ->
            val side = faceOrder[index]
            side to VsSide(
                texture = "#$texture",
                uv = uvForFace(mesh, face, textureScale, side),
                autoUv = null,
                enabled = null,
                rotation = null,
                snapUv = null
            )
        }.toMap()

        return VsElement(
            name = obj.name,
            comment = null,
            shade = null,
            tintIndex = null,
            renderPass = null,
            unwrapMode = null,
            unwrapRotation = null,
            autoUnwrap = null,
            uv = null,
            rotationOrigin = if (rot != null) trs.translation else null,
            rotationX = rot?.xd,
            rotationY = rot?.yd,
            rotationZ = rot?.zd,
            from = from,
            to = to,
            faces = faces,
            children = emptyList(),
            attachmentpoints = emptyList()
        )
    }

    private fun uvForFace(mesh: IMesh, face: IFaceIndex, textureScale: IVector2, side: VsEnumSide): IVector4 {
        val (x, y) = when (side) {
            VsEnumSide.north -> Pair(true, false)
            VsEnumSide.east -> Pair(true, false)
            VsEnumSide.south -> Pair(true, true)
            VsEnumSide.west -> Pair(true, true)
            VsEnumSide.up -> Pair(false, true)
            VsEnumSide.down -> Pair(false, false)
        }

        val xMin = mesh.tex[face.tex[0]].xd
        val yMin = mesh.tex[face.tex[0]].yd
        val xMax = mesh.tex[face.tex[2]].xd
        val yMax = mesh.tex[face.tex[2]].yd

        return vec4Of(
            (if (x) xMax else xMin) * textureScale.xi,
            (if (y) yMax else yMin) * textureScale.yi,
            (if (x) xMin else xMax) * textureScale.xi,
            (if (y) yMin else yMax) * textureScale.yi
        )
    }

    private fun MutableGroupTree.toElement(model: IModel, textureScale: IVector2, warnings: MutableList<String>): VsElement? {

        val group = model.getGroup(this.group)
        if (group == GroupNone) return null
        if (objects.isEmpty() && children.isEmpty()) return null

        val children = this.objects.mapNotNull { it.toElement(model, textureScale, warnings) } +
            this.children.mapNotNull { it.toElement(model, textureScale, warnings) }

        val trs = group.transform.toTRS()
        val from = trs.translation
        val rot = trs.euler.angles.let { if (it == Vector3.ZERO) null else it }

        return VsElement(
            name = group.name,
            comment = null,
            shade = null,
            tintIndex = null,
            renderPass = null,
            unwrapMode = null,
            unwrapRotation = null,
            autoUnwrap = null,
            uv = null,
            rotationOrigin = if (rot != null) trs.translation else null,
            rotationX = rot?.xd,
            rotationY = rot?.yd,
            rotationZ = rot?.zd,
            from = from,
            to = from,
            faces = emptyMap(),
            children = children,
            attachmentpoints = emptyList()
        )
    }

    private fun texturePath(path: ResourcePath): String {
        val strPath = path.uri.toASCIIString()
        return strPath.substringBefore('.').substringAfterLast('/')
    }

    private fun textureName(name: String?): String {
        if (name == null) return "null"
        return name.substringBefore('.').substringAfterLast('/').toLowerCase()
    }
}

private data class VsModel(
    val editor: Map<String, Any>,
    val ambientocclusion: Boolean?,
    val textureWidth: Int,
    val textureHeight: Int,
    val textures: Map<String, String>,
    val elements: List<VsElement>,
    val animations: List<Any>
)

private data class VsElement(
    val name: String,

    // (Optional)
    val comment: String?,

    // (Optional)
    val shade: Boolean? = false,
    val tintIndex: Int? = 0,
    val renderPass: Int? = -1,

    // EntityTextureMode (Optional)
    val unwrapMode: Int?,
    val unwrapRotation: Int?,
    val autoUnwrap: Int?,
    val uv: IVector2?,

    // (Optional)
    val rotationOrigin: IVector3?,
    val rotationX: Double?,
    val rotationY: Double?,
    val rotationZ: Double?,

    val from: IVector3,
    val to: IVector3,
    val faces: Map<VsEnumSide, VsSide>,

    // (Optional)
    val children: List<VsElement>,
    val attachmentpoints: List<VsAttachmentPoints>
)

private data class VsSide(
    val texture: String,
    val uv: IVector4,
    val snapUv: Boolean?,
    val rotation: Int?,
    val enabled: Boolean?,
    val autoUv: Boolean?
)

private data class VsAttachmentPoints(
    val code: String,
    val posX: Double,
    val posY: Double,
    val posZ: Double,
    val rotationX: Double,
    val rotationY: Double,
    val rotationZ: Double
)

private enum class VsEnumSide {
    north,
    east,
    south,
    west,
    up,
    down,
}