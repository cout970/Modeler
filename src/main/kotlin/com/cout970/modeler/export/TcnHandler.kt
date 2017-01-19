package com.cout970.modeler.export

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.log.print
import com.cout970.modeler.model.*
import com.cout970.modeler.modeleditor.rotatePointAroundPivot
import com.cout970.modeler.util.ResourcePath
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by cout970 on 2017/01/12.
 */

class TcnImporter {

    val cubeTypes = listOf("d9e621f7-957f-4b77-b1ae-20dcd0da7751", "de81aa14-bd60-4228-8d8d-5238bcd3caaa")
    val COMMA = ",".toRegex()

    fun import(path: ResourcePath): Model {
        val model = path.enterZip("model.xml")
        val stream = model.inputStream()
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(stream)

        var texture: Material = MaterialNone
        val nodeListModel = document.getElementsByTagName("Model")
        if (nodeListModel.length < 1) throw IllegalStateException(
                "Tcn Model contains no Model tag, (no models in the file)")

        nodeListModel.item(0).attributes ?: throw IllegalStateException(
                "Tcn Model contains a Model tag with no attributes")

        val textureName = nodeListModel.item(0).attributes.getNamedItem("texture")
        if (textureName != null) {
            texture = TexturedMaterial(textureName.textContent, path.enterZip(textureName.textContent))
        }

        val textureDim = document.getElementsByTagName("TextureSize")
        var textureSize: IVector2 = vec2Of(32)
        if (textureDim.length > 0) {
            try {
                val tmp = textureDim.item(0).textContent.split(COMMA).dropLastWhile(String::isEmpty)
                if (tmp.size == 2) {
                    textureSize = vec2Of(tmp[0].toInt(), tmp[1].toInt())
                }
            } catch (e: NumberFormatException) {
                throw IllegalStateException("Tcn Model contains a TextureSize tag with invalid data")
            }
        }

        val shapes = document.getElementsByTagName("Shape")
        val meshes = mutableListOf<Mesh>()

        for (i in 0..shapes.length - 1) {
            val shape = shapes.item(i)
            val shapeAttributes = shape.attributes ?:
                                  throw IllegalStateException(
                                          "Shape #" + (i + 1) + " has no attributes")

            var shapeType: String? = null
            val type = shapeAttributes.getNamedItem("type")
            if (type != null) {
                shapeType = type.nodeValue
            }
            if (shapeType != null && !cubeTypes.contains(shapeType)) {
                continue
            }

            try {
                meshes += getMesh(shape, textureSize)
            } catch (e: NumberFormatException) {
                log(Level.ERROR) { "Tcn file contains malformed integers within its data, ignoring" }
                e.print()
            }
        }
        return Model(listOf(ModelGroup(meshes, name = "TcnModel", material = texture)))
    }

    @Throws(NumberFormatException::class)
    private fun getMesh(shape: Node, textureSize: IVector2): Mesh {
        var mirrored = false
        var offset: List<String> = listOf()
        var position: List<String> = listOf()
        var rotation: List<String> = listOf()
        var size: List<String> = listOf()
        var textureOffset: List<String> = listOf()

        val shapeChildren = shape.childNodes
        //extract properties
        for (j in 0..shapeChildren.length - 1) {

            val shapeChild = shapeChildren.item(j)
            val shapeChildName = shapeChild.nodeName
            var shapeChildValue: String? = shapeChild.textContent

            if (shapeChildValue != null) {
                shapeChildValue = shapeChildValue.trim { it <= ' ' }

                when (shapeChildName) {
                    "IsMirrored" -> mirrored = shapeChildValue != "False"
                    "Offset" -> offset = shapeChildValue.split(COMMA).dropLastWhile(String::isEmpty)
                    "Position" -> position = shapeChildValue.split(COMMA).dropLastWhile(String::isEmpty)
                    "Rotation" -> rotation = shapeChildValue.split(COMMA).dropLastWhile(String::isEmpty)
                    "Size" -> size = shapeChildValue.split(COMMA).dropLastWhile(String::isEmpty)
                    "TextureOffset" -> textureOffset = shapeChildValue.split(COMMA).dropLastWhile(
                            String::isEmpty)
                }
            }
        }

        val vSize = vec3Of(size[0].toInt(), size[1].toInt(), size[2].toInt())
        val vOffset = vec3Of(offset[0].toFloat(), offset[1].toFloat(), offset[2].toFloat())
        val vPos = vec3Of(position[0].toFloat(), position[1].toFloat(), position[2].toFloat())
        val vTexture = vec2Of(textureOffset[0].toInt(), textureOffset[1].toInt())
        val vRotation = vec3Of(rotation[0].toFloat(), rotation[1].toFloat(), rotation[2].toFloat())

        val rSize = vSize
        val rOffset = vOffset * vec3Of(1, -1, 1)
        val rPos = vPos * vec3Of(1, -1, 1) - vec3Of(0, vSize.y, 0)
        val rTexture = vTexture
        val rRotation = vRotation.toRadians() * vec3Of(-1, 1, -1)
        val rRotPoint = vPos * vec3Of(1, -1, 1) + vec3Of(8, 24, 8)

        val fOffset = rPos + rOffset + vec3Of(8, 24, 8)

//          cube.rotationPoint.set(cubePosition0.copy().add(8, 24, 8))
//          cube.rotation.set(cubeRotation)
//          cube.textureOffset.set(cubeTextureOffset)
//          cube.flipUV = mirrored

        val cube = createCube(size = rSize, offset = fOffset, textureOffset = rTexture, textureSize = textureSize)
        if (rRotation.lengthSq() == 0.0) return cube
        return cube.copy(cube.positions.map {
            rotatePointAroundPivot(it, rRotPoint, rRotation)
        })
    }

    fun createCube(size: IVector3, offset: IVector3, textureOffset: IVector2 = Vector2.ORIGIN,
                   textureSize: IVector2 = vec2Of(32, 32)): Mesh {
        val n: IVector3 = vec3Of(0) + offset
        val p: IVector3 = size + offset

        val width = size.xd
        val height = size.yd
        val length = size.zd

        val offsetX = textureOffset.xd
        val offsetY = textureOffset.yd

        val texelSize = vec2Of(1) / textureSize

        val quads = listOf(
                //negX West
                Quad.create(
                        vec3Of(n.x, n.y, p.z),
                        vec3Of(n.x, p.y, p.z),
                        vec3Of(n.x, p.y, n.z),
                        vec3Of(n.x, n.y, n.z)
                ).setTexture1(
                        vec2Of(offsetX + length + width + length, offsetY + length + height) * texelSize,
                        vec2Of(offsetX + length + width, offsetY + length) * texelSize
                ),
                //posX East
                Quad.create(
                        vec3Of(p.x, p.y, n.z),
                        vec3Of(p.x, p.y, p.z),
                        vec3Of(p.x, n.y, p.z),
                        vec3Of(p.x, n.y, n.z)
                ).setTexture(
                        vec2Of(offsetX + length, offsetY + length + height) * texelSize,
                        vec2Of(offsetX, offsetY + length) * texelSize
                ),
                //negY Down
                Quad.create(
                        vec3Of(p.x, n.y, n.z),
                        vec3Of(p.x, n.y, p.z),
                        vec3Of(n.x, n.y, p.z),
                        vec3Of(n.x, n.y, n.z)
                ).setTexture1(
                        vec2Of(offsetX + length + width + width, offsetY) * texelSize,
                        vec2Of(offsetX + length + width, offsetY + length) * texelSize
                ),
                //posY Up
                Quad.create(
                        vec3Of(n.x, p.y, p.z),
                        vec3Of(p.x, p.y, p.z),
                        vec3Of(p.x, p.y, n.z),
                        vec3Of(n.x, p.y, n.z)
                ).setTexture(
                        vec2Of(offsetX + length + width, offsetY + length) * texelSize,
                        vec2Of(offsetX + length, offsetY) * texelSize
                ),
                //negZ North
                Quad.create(
                        vec3Of(n.x, p.y, n.z),
                        vec3Of(p.x, p.y, n.z),
                        vec3Of(p.x, n.y, n.z),
                        vec3Of(n.x, n.y, n.z)
                ).setTexture(
                        vec2Of(offsetX + length + width, offsetY + length + height) * texelSize,
                        vec2Of(offsetX + length, offsetY + length) * texelSize
                ),
                //posZ South
                Quad.create(
                        vec3Of(p.x, n.y, p.z),
                        vec3Of(p.x, p.y, p.z),
                        vec3Of(n.x, p.y, p.z),
                        vec3Of(n.x, n.y, p.z)
                ).setTexture1(
                        vec2Of(offsetX + length + width + length, offsetY + length) * texelSize,
                        vec2Of(offsetX + length + width + length + width, offsetY + length + height) * texelSize
                )
        )
        return Mesh.quadsToMesh(quads)
    }

    fun Quad.setTexture(uv0: IVector2, uv1: IVector2): Quad {
        return Quad(
                a.copy(tex = vec2Of(uv1.x, uv0.y)),
                b.copy(tex = vec2Of(uv0.x, uv0.y)),
                c.copy(tex = vec2Of(uv0.x, uv1.y)),
                d.copy(tex = vec2Of(uv1.x, uv1.y))
        )
    }

    fun Quad.setTexture1(uv0: IVector2, uv1: IVector2): Quad {
        return Quad(
                a.copy(tex = vec2Of(uv1.x, uv1.y)),
                b.copy(tex = vec2Of(uv1.x, uv0.y)),
                c.copy(tex = vec2Of(uv0.x, uv0.y)),
                d.copy(tex = vec2Of(uv0.x, uv1.y))
        )
    }
}