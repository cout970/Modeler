package com.cout970.modeler.core.export

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.animation.AnimationTargetGroup
import com.cout970.modeler.api.animation.AnimationTargetObject
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.tool.Animator
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2016/12/25.
 */
class ObjExporter {

    fun export(file: File, model: IModel, gui: Gui, args: ObjExportProperties) {

        val output = file.outputStream()
        val vertex = LinkedList<IVector3>()
        val vertexMap = HashSet<IVector3>()

        val texCoords = LinkedList<IVector2>()
        val texCoordsMap = HashSet<IVector2>()

        val normals = LinkedList<IVector3>()
        val normalsMap = LinkedHashSet<IVector3>()

        val groups = mutableListOf<ObjGroup>()

        val matrixCache = mutableMapOf<IObjectRef, IMatrix4>()
        val animator = gui.animator
        val animation = model.animationMap[gui.programState.selectedAnimation] ?: animator.animation

        getRecursiveMatrix(matrixCache, model, animator, animation)

        model.objects.forEach { obj ->

            val quads = mutableListOf<ObjQuad>()
            val mesh = obj.mesh.transform(matrixCache[obj.ref] ?: Matrix4.IDENTITY)

            mesh.faces.forEach { face ->
                val objQuad = ObjQuad()

                for (i in 0 until face.vertexCount) {
                    val vertPos = mesh.pos[face.pos[i]]
                    val vertTex = mesh.tex[face.tex[i]]

                    if (vertexMap.contains(vertPos)) {
                        objQuad.vertexIndices[i] = vertex.indexOf(vertPos) + 1
                    } else {
                        objQuad.vertexIndices[i] = vertex.size + 1
                        vertex.add(vertPos)
                        vertexMap.add(vertPos)
                    }

                    if (texCoordsMap.contains(vertTex)) {
                        objQuad.textureIndices[i] = texCoords.indexOf(vertTex) + 1
                    } else {
                        objQuad.textureIndices[i] = texCoords.size + 1
                        texCoords.add(vertTex)
                        texCoordsMap.add(vertTex)
                    }
                }

                if (args.useNormals) {
                    val (a, b, c, d) = face.pos.map { mesh.pos[it] }
                    val ac = c - a
                    val bd = d - b
                    val normal = (ac cross bd).normalize()

                    if (normalsMap.contains(normal)) {
                        val index = normals.indexOf(normal) + 1
                        objQuad.normalIndices[0] = index
                        objQuad.normalIndices[1] = index
                        objQuad.normalIndices[2] = index
                        objQuad.normalIndices[3] = index
                    } else {
                        val index = normals.size + 1
                        objQuad.normalIndices[0] = index
                        objQuad.normalIndices[1] = index
                        objQuad.normalIndices[2] = index
                        objQuad.normalIndices[3] = index
                        normals.add(normal)
                        normalsMap.add(normal)
                    }
                }

                quads += objQuad
            }
            groups.add(ObjGroup(obj.name, obj.id.toString(), model.getMaterial(obj.material).name, quads))
        }

        val sym = DecimalFormatSymbols().apply { decimalSeparator = '.' }
        val format = DecimalFormat("####0.000000", sym)

        val writer = output.writer()

        writer.write("mtllib ${args.materialLib}.mtl\n")

        // The program stores positions multiplied by 16 to avoid working with decimals
        // but the values must be converted back to the original size
        for (a in vertex.map { it * (1.0 / 16.0) }) {
            writer.write(String.format("v %s %s %s\n", format.format(a.xd), format.format(a.yd), format.format(a.zd)))
        }
        writer.append('\n')
        if (args.flipUV) {
            for (a in texCoords) {
                writer.write(String.format("vt %s %s\n", format.format(a.xd), format.format(1 - a.yd)))
            }
        } else {
            for (a in texCoords) {
                writer.write(String.format("vt %s %s\n", format.format(a.xd), format.format(a.yd)))
            }
        }
        writer.append('\n')

        if (args.useNormals) {
            for (a in normals) {
                writer.write(String.format("vn %s %s %s\n", format.format(a.xd), format.format(a.yd), format.format(a.zd)))
            }
            writer.append('\n')
        }

        for (group in groups) {
            writer.write("\nusemtl ${group.material.replace(' ', '_').toLowerCase()}\n")
            writer.append("# id ${group.ref}\n")
            writer.append("g ${group.name.replace(' ', '_').toLowerCase()}\n")
            for (quad in group.quads) {
                val a = quad.vertexIndices
                val b = quad.textureIndices
                val c = quad.normalIndices
                writer.write(String.format("f %d/%d/%d %d/%d/%d %d/%d/%d %d/%d/%d\n",
                    a[0], b[0], c[0], a[1], b[1], c[1],
                    a[2], b[2], c[2], a[3], b[3], c[3]))
            }

        }
        writer.append('\n')

        writer.flush()
        writer.close()

        // Animations
        if (model.animationMap.isEmpty()) return
        val json = JsonObject()

        model.animationMap.forEach { (ref, anim) ->
            val jsonAnim = JsonObject()
            jsonAnim.addProperty("name", anim.name)
            jsonAnim.addProperty("length", anim.timeLength)
            val channels = JsonObject()

            anim.channels.forEach { (id, channel) ->
                val jsonChannel = JsonObject()
                jsonChannel.addProperty("name", channel.name)
                jsonChannel.addProperty("enabled", channel.enabled)
                jsonChannel.addProperty("interpolation", channel.interpolation.toString().toLowerCase())
                val keyframes = JsonArray()

                channel.keyframes.forEach { key ->
                    val frame = JsonObject()
                    frame.addProperty("time", key.time)
                    frame.add("position", key.value.translation.toJson())
                    frame.add("quaternion_rotation", key.value.rotation.toJson())
                    frame.add("euler_rotation", key.value.euler.angles.toJson())
                    frame.add("scale", key.value.scale.toJson())

                    keyframes.add(frame)
                }

                jsonChannel.add("keyframes", keyframes)

                channels.add(id.id.toString(), jsonChannel)
            }

            jsonAnim.add("channels", channels)

            val channelMappings = JsonObject()

            anim.channelMapping.forEach { (ref, value) ->
                val target = when (value) {
                    is AnimationTargetGroup -> ref.id.toString()
                    is AnimationTargetObject -> ref.id.toString()
                }
                channelMappings.addProperty(ref.id.toString(), target)
            }

            jsonAnim.add("channel_mappings", channelMappings)

            json.add(ref.id.toString(), jsonAnim)
        }

        val textJson = GsonBuilder().setPrettyPrinting().create().toJson(json)
        File(file.absoluteFile.parent, file.nameWithoutExtension + "_anim.json").writeText(textJson)
    }

    private fun IVector3.toJson() = JsonObject().apply {
        addProperty("x", xd)
        addProperty("y", yd)
        addProperty("z", zd)
    }

    private fun IQuaternion.toJson() = JsonObject().apply {
        addProperty("x", xd)
        addProperty("y", yd)
        addProperty("z", zd)
        addProperty("w", wd)
    }

    private fun getRecursiveMatrix(matrixCache: MutableMap<IObjectRef, IMatrix4>, model: IModel,
                                   animator: Animator, animation: IAnimation) {

        model.tree.objects[RootGroupRef].forEach { obj ->
            matrixCache[obj] = animator.animate(animation, obj, model.getObject(obj).transformation).matrix
        }

        model.tree.groups[RootGroupRef].forEach {
            getRecursiveMatrix(matrixCache, model, it, Matrix4.IDENTITY, animator, animation)
        }
    }

    private fun getRecursiveMatrix(matrixCache: MutableMap<IObjectRef, IMatrix4>, model: IModel,
                                   group: IGroupRef, matrix: IMatrix4, animator: Animator, animation: IAnimation) {

        val mat = matrix * animator.animate(animation, group, model.getGroup(group).transform).matrix

        model.tree.objects[group].forEach { obj ->
            matrixCache[obj] = mat * animator.animate(animation, obj, model.getObject(obj).transformation).matrix
        }

        model.tree.groups[group].forEach {
            getRecursiveMatrix(matrixCache, model, it, mat, animator, animation)
        }
    }

}

class ObjImporter {

    internal val separator = "/"
    internal val sVertex = "v "
    internal val sNormal = "vn "
    internal val sTexture = "vt "
    internal val sTexture2 = "vtc "
    internal val sFace = "f "
    internal val sGroup = "g "
    internal val sObject = "o "
    internal val sMaterial = "usemtl "
    internal val sLib = "mtllib "
    internal val sNewMaterial = "newmtl "
    internal val sMap_Ka = "map_Ka "
    internal val sMap_Kd = "map_Kd "
    internal val sComment = "#"
    internal val startIndex = 1 //index after label

    fun import(path: ResourcePath, flipUvs: Boolean): IModel {

        val (data, groups, objMaterials) = parseFile(path, flipUvs)

        val materials = objMaterials.toSet().map { it.toMaterial() }
        val materialMap = materials.associate { mat -> mat.name to mat.ref }

        val objs = groups.map { group ->
            Object(name = group.name,
                mesh = group.toMesh(data).optimize(),
                material = materialMap[group.material] ?: MaterialRefNone
            )
        }

        return Model.of(objs, materials)
    }

    fun importAsMesh(path: ResourcePath, flipUvs: Boolean): IMesh {
        val (data, groups) = parseFile(path, flipUvs)
        groups.firstOrNull() ?: return Mesh()

        return groups
            .map { objGroup -> objGroup.toMesh(data) }
            .reduce { acc, iMesh -> acc.merge(iMesh) }
            .optimize()
    }

    private fun ObjGroup.toMesh(data: MeshData): IMesh {

        val faces = quads.map {
            FaceIndex.from(it.vertexIndices.toList(), it.textureIndices.toList())
        }
        var pos = data.vertices.map { it * 16 }
        var tex = data.texCoords
        if (faces.isNotEmpty()) {
            if (pos.isEmpty()) pos += Vector3.ORIGIN
            if (tex.isEmpty()) tex += Vector2.ORIGIN
        }
        return Mesh(pos, tex, faces)
    }


    private fun parseFile(path: ResourcePath, flipUvs: Boolean): Triple<MeshData, List<ObjGroup>, List<ObjMaterial>> {
        require(path.isValid()) { "Invalid path: $path" }

        val input = path.inputStream()
        val vertices = mutableListOf<IVector3>()
        val texCoords = mutableListOf<IVector2>()
        val normals = mutableListOf<IVector3>()
        var hasTextures = false
        var hasNormals = false

        val noGroup = ObjGroup("noGroup", "", "noTexture", mutableListOf())
        val groups = mutableListOf<ObjGroup>()
        var quads = noGroup.quads
        var currentMaterial = "material"
        val materials = mutableListOf<ObjMaterial>()

        val lines = input.reader().readLines()

        for (line in lines) {
            val lineSpliced = line.split(" ")

            if (line.startsWith(sVertex)) { //vertex
                //reads a vertex
                vertices.add(vec3Of(lineSpliced[startIndex].toFloat(),
                    lineSpliced[startIndex + 1].toFloat(),
                    lineSpliced[startIndex + 2].toFloat()))

            } else if (line.startsWith(sNormal)) { //normals

                hasNormals = true
                //read normals
                normals.add(vec3Of(lineSpliced[startIndex].toFloat(),
                    lineSpliced[startIndex + 1].toFloat(),
                    lineSpliced[startIndex + 2].toFloat()))

            } else if (line.startsWith(sTexture) || line.startsWith(sTexture2)) { //textures

                hasTextures = true
                //reads a texture coords
                texCoords.add(vec2Of(lineSpliced[startIndex].toFloat(),
                    if (flipUvs)
                        1 - lineSpliced[startIndex + 1].toFloat()
                    else
                        lineSpliced[startIndex + 1].toFloat()))

            } else if (line.startsWith(sFace)) { //faces
                val quad = ObjQuad()
                for (i in 1..4) {
                    val textVertex = if (i in lineSpliced.indices) lineSpliced[i] else lineSpliced[lineSpliced.size - 1]
                    val index = textVertex.split(separator)

                    quad.vertexIndices[i - 1] = index[0].toInt() - 1
                    if (hasTextures) {
                        quad.textureIndices[i - 1] = index[1].toInt() - 1
                        if (hasNormals) {
                            quad.normalIndices[i - 1] = index[2].toInt() - 1
                        }
                    } else {
                        if (hasNormals) {
                            quad.textureIndices[i - 1] = 0
                            quad.normalIndices[i - 1] = index[2].toInt() - 1
                        }
                    }
                }
                quads.add(quad)

            } else if (line.startsWith(sGroup) || line.startsWith(sObject)) {
                val newGroup = ObjGroup(lineSpliced[1], "", currentMaterial, mutableListOf())
                quads = newGroup.quads
                groups.add(newGroup)

            } else if (line.startsWith(sMaterial)) {
                currentMaterial = lineSpliced[1]
                noGroup.material = currentMaterial

            } else if (line.startsWith(sLib)) {
                try {
                    materials.addAll(parseMaterialLib(path.parent!!, lineSpliced[1]))
                } catch (e: Exception) {
                    log(Level.ERROR) { "Error reading the material library: ${e.message}" }
                }
            } else if (!line.startsWith(sComment) && line.isNotEmpty()) {
                if (lineSpliced[0] !in setOf("s")) {
                    log(Level.ERROR) { "Invalid line parsing OBJ ($path): '$line'" }
                }
            }
        }
        if (noGroup.quads.isNotEmpty()) {
            groups.add(noGroup)
        }
        return Triple(MeshData(vertices, texCoords, normals), groups, materials)
    }

    private fun parseMaterialLib(resource: ResourcePath, name: String): List<ObjMaterial> {
        val text = resource.resolve(name).inputStream().reader().readLines()

        val materialList = mutableListOf<ObjMaterial>()
        var material: ObjMaterial? = null
        for (line_ in text.asSequence()) {
            val line = line_.replace("\r", "")
            val lineSpliced = line.split(" ")

            if (line.startsWith(sNewMaterial)) {
                material?.let { materialList += it }
                material = ObjMaterial(lineSpliced[1])
            } else if (line.startsWith(sMap_Ka) || line.startsWith(sMap_Kd)) {
                try {
                    val subPath = if (lineSpliced[1].contains(":")) {
                        val slash = lineSpliced[1].substringAfter("/")
                        val textureName = if (slash.isEmpty()) lineSpliced[1].substringAfter(":") else slash
                        "textures/$textureName.png"
                    } else {
                        lineSpliced[1] + ".png"
                    }
                    material!!.map_Ka = resource.toPath().resolve(subPath).toString()
                } catch (e: Exception) {
                    e.print()
                }
            } else if (!line.startsWith(sComment) && line.isNotEmpty()) {
                // Ignoring line
            }
        }
        material?.let { materialList += it }
        return materialList
    }
}

private data class ObjMaterial(val name: String) {
    var map_Ka: String = ""

    fun toMaterial(): IMaterial = TexturedMaterial(name, ResourcePath.fromResourceLocation(map_Ka))
}

private class MeshData(
    val vertices: List<IVector3>,
    val texCoords: List<IVector2>,
    val normals: List<IVector3>
)

private class ObjGroup(
    val name: String,
    val ref: String,
    var material: String,
    val quads: MutableList<ObjQuad>
)

private class ObjQuad {
    var vertexIndices: IntArray
        internal set
    var textureIndices: IntArray
        internal set
    var normalIndices: IntArray
        internal set

    init {
        this.vertexIndices = IntArray(4)
        this.textureIndices = IntArray(4)
        this.normalIndices = IntArray(4)
    }
}

