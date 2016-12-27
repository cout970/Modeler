package com.cout970.modeler.export

import com.cout970.matrix.extensions.times
import com.cout970.modeler.model.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2016/12/25.
 */
class ObjExporter {

    fun export(output: OutputStream, model: Model, mtllib: String) {

        val vertex = LinkedList<IVector3>()
        val vertexMap = LinkedHashSet<IVector3>()

        val texCoords = LinkedList<IVector2>()
        val texCoordsMap = LinkedHashSet<IVector2>()

        val normals = LinkedList<IVector3>()
        val normalsMap = LinkedHashSet<IVector3>()

        val objects: MutableList<ObjObject> = mutableListOf()

        // begin model conversion
        for (obj in model.objects) {
            val groups = mutableListOf<ObjGroup>()

            for (group in obj.groups) {

                val quads = mutableListOf<ObjQuad>()

                for (mesh in group.meshes) {

                    val matrix = obj.transform.matrix * group.transform.matrix * mesh.transform.matrix
                    val rawQuads = mesh.getQuads().map { it.transform(matrix) }

                    for (rawQuad in rawQuads) {
                        val objQuad = ObjQuad()

                        for ((i, vertex1) in rawQuad.vertex.withIndex()) {
                            val vertPos = vertex1.pos

                            if (vertexMap.contains(vertPos)) {
                                objQuad.vertexIndices[i] = vertex.indexOf(vertPos) + 1
                            } else {
                                objQuad.vertexIndices[i] = vertex.size + 1
                                vertex.add(vertPos)
                                vertexMap.add(vertPos)
                            }

                            val vertTex = vertex1.tex
                            if (texCoordsMap.contains(vertTex)) {
                                objQuad.textureIndices[i] = texCoords.indexOf(vertTex) + 1
                            } else {
                                objQuad.textureIndices[i] = texCoords.size + 1
                                texCoords.add(vertTex)
                                texCoordsMap.add(vertTex)
                            }

                            val vertNorm = rawQuad.normal
                            if (normalsMap.contains(vertNorm)) {
                                objQuad.normalIndices[i] = normals.indexOf(vertNorm) + 1
                            } else {
                                objQuad.normalIndices[i] = normals.size + 1
                                normals.add(vertNorm)
                                normalsMap.add(vertNorm)
                            }
                        }
                        quads += objQuad
                    }
                }
                groups += ObjGroup(group.name, quads)
            }
            objects += ObjObject(obj.name, obj.material.name, groups)
        }
        //end of model conversion

        val sym = DecimalFormatSymbols()
        sym.decimalSeparator = '.'
        val format = DecimalFormat("####0.000000", sym)

        val writer = output.writer()

        writer.write("mtllib $mtllib.mtl\n")

        for (a in vertex) {
            writer.write(String.format("v %s %s %s\n", format.format(a.xd), format.format(a.yd), format.format(a.zd)))
        }
        writer.append('\n')
        for (a in texCoords) {
            writer.write(String.format("vt %s %s\n", format.format(a.xd), format.format(a.yd)))
        }
        writer.append('\n')
        for (a in normals) {
            writer.write(String.format("vn %s %s %s\n", format.format(a.xd), format.format(a.yd), format.format(a.zd)))
        }
        for (obj in objects) {
            if (obj.groups.isNotEmpty()) {
                writer.write("\no ${obj.name.replace(' ', '_')}\n")
                writer.write("usemtl ${obj.material.replace(' ', '_')}\n\n")
                for (group in obj.groups) {
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
    internal val sFace = "f "
    internal val sGroup = "g "
    internal val sObject = "o "
    internal val sMaterial = "usemtl "
    internal val sComment = "#"
    internal val startIndex = 1 //index after label

    fun import(input: InputStream): Model {

        val vertices = mutableListOf<IVector3>()
        val texCoords = mutableListOf<IVector2>()
        val normals = mutableListOf<IVector3>()
        var hasTextures = false
        var hasNormals = false

        val noGroup = ObjGroup("noGroup", mutableListOf())
        val noObj = ObjObject("noObject", "noTexture", mutableListOf())
        val objects = mutableListOf<ObjObject>()
        var groups = noObj.groups
        var quads = noGroup.quads
        var material = "noTexture"

        for (line in input.reader().readLines()) {
            val p = line.split(" ")

            if (line.startsWith(sVertex)) { //vertex

                //reads a vertex
                vertices.add(vec3Of(p[startIndex].toFloat(),
                        p[startIndex + 1].toFloat(),
                        p[startIndex + 2].toFloat()))

            } else if (line.startsWith(sNormal)) { //normals

                hasNormals = true
                //read normals
                normals.add(vec3Of(p[startIndex].toFloat(),
                        p[startIndex + 1].toFloat(),
                        p[startIndex + 2].toFloat()))
            } else if (line.startsWith(sTexture)) { //textures

                hasTextures = true
                //reads a texture coords
                texCoords.add(vec2Of(p[startIndex].toFloat(),
                        p[startIndex + 1].toFloat()))

            } else if (line.startsWith(sFace)) { //faces
                val quad = ObjQuad()
                for (i in 1..4) {
                    val chunk = p[i]
                    val comp = chunk.split(separator)

                    quad.vertexIndices[i - 1] = comp[0].toInt() - 1
                    if (hasTextures) {
                        quad.textureIndices[i - 1] = comp[1].toInt() - 1
                        if (hasNormals) {
                            quad.normalIndices[i - 1] = comp[2].toInt() - 1
                        }
                    } else {
                        if (hasNormals) {
                            quad.normalIndices[i - 1] = comp[1].toInt() - 1
                        }
                    }
                }
                quads.add(quad)
            } else if (line.startsWith(sGroup)) {
                val newGroup = ObjGroup(p[1], mutableListOf())
                quads = newGroup.quads
                groups.add(newGroup)

            } else if (line.startsWith(sObject)) {
                val newObj = ObjObject(p[1], material, mutableListOf())
                groups = newObj.groups
                objects.add(newObj)

            } else if (line.startsWith(sMaterial)) {
                material = p[1]
            } else if (!line.startsWith(sComment) && !line.isEmpty()) {
                println("Ignoring line: '$line'\n")
            }
        }
        if (noGroup.quads.isNotEmpty()) {
            objects.add(ObjObject("noGroupObject", "noTexture", mutableListOf(noGroup)))
        }
        if (noObj.groups.isNotEmpty()) {
            objects.add(noObj)
        }

        return Model(
                objects.map { obj ->
                    ModelObject(name = obj.name, material = Material.TexturedMaterial(obj.material), groups = obj.groups.map { group ->
                        ModelGroup(name = group.name, transform = Transformation.IDENTITY, meshes = listOf(Mesh(
                                vertices, texCoords, quads.map {
                            QuadIndices(
                                    it.vertexIndices[0], it.textureIndices[0],
                                    it.vertexIndices[1], it.textureIndices[1],
                                    it.vertexIndices[2], it.textureIndices[2],
                                    it.vertexIndices[3], it.textureIndices[3])
                        }
                        )))
                    }, transform = Transformation.IDENTITY)
                }
        )
    }
}

private class ObjObject(
        val name: String,
        val material: String,
        val groups: MutableList<ObjGroup>
)

private class ObjGroup(
        val name: String,
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

