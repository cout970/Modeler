package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.mesh.IMesh
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
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2016/12/25.
 */
class ObjExporter {

    fun export(output: OutputStream, model: IModel, args: ObjExportProperties) {

        val vertex = LinkedList<IVector3>()
        val vertexMap = LinkedHashSet<IVector3>()

        val texCoords = LinkedList<IVector2>()
        val texCoordsMap = LinkedHashSet<IVector2>()

        val normals = LinkedList<IVector3>()
        val normalsMap = LinkedHashSet<IVector3>()

        val groups = mutableListOf<ObjGroup>()

        model.objects.forEach { obj ->

            val quads = mutableListOf<ObjQuad>()
            obj.mesh.faces.forEach { face ->
                val objQuad = ObjQuad()

                for (i in 0 until face.vertexCount) {
                    val vertPos = obj.mesh.pos[face.pos[i]]
                    val vertTex = obj.mesh.tex[face.tex[i]]

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
                    val (a, b, c, d) = face.pos.map { obj.mesh.pos[it] }
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
            groups.add(ObjGroup(obj.name, model.getMaterial(obj.material).name, quads))
        }

        val sym = DecimalFormatSymbols().apply { decimalSeparator = '.' }
        val format = DecimalFormat("####0.000000", sym)

        val writer = output.writer()

        writer.write("mtllib ${args.materialLib}.mtl\n")

        for (a in vertex.map { it * 0.0625 }) {
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
            writer.write("usemtl ${group.material.replace(' ', '_')}\n\n")
            writer.append("g ${group.name.replace(' ', '_')}\n")
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

        val noGroup = ObjGroup("noGroup", "noTexture", mutableListOf())
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
                val newGroup = ObjGroup(lineSpliced[1], currentMaterial, mutableListOf())
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
            } else if (!line.startsWith(sComment) && !line.isEmpty()) {
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
                    val subPath: String
                    if (lineSpliced[1].contains(":")) {
                        val slash = lineSpliced[1].substringAfter("/")
                        subPath = "textures/" + (if (slash.isEmpty()) lineSpliced[1].substringAfter(
                                ":") else slash) + ".png"
                    } else {
                        subPath = lineSpliced[1] + ".png"
                    }
                    material!!.map_Ka = resource.toPath().resolve(subPath).toString()
                } catch (e: Exception) {
                    e.print()
                }
            } else if (!line.startsWith(sComment) && !line.isEmpty()) {
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

