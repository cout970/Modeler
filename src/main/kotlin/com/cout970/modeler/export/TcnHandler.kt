package com.cout970.modeler.export

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.log.print
import com.cout970.modeler.model.*
import com.cout970.modeler.modeleditor.rotatePointAroundPivot
import com.cout970.vector.extensions.*
import org.w3c.dom.Node
import java.awt.Dimension
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by cout970 on 2017/01/12.
 */

class TcnImporter {

    val cubeTypes = listOf("d9e621f7-957f-4b77-b1ae-20dcd0da7751", "de81aa14-bd60-4228-8d8d-5238bcd3caaa")

    private fun readModel(input: InputStream, func: (InputStream) -> Model): Model {
        val stream = ZipInputStream(input)
        while (true) {
            val entry = stream.nextEntry ?: break
            if (entry.name == "model.xml") {
                return func(stream)
            }
        }
        throw IllegalStateException("Invalid tcn file")
    }

    fun import(input: InputStream): Model {
        return readModel(input) { stream ->
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document = documentBuilder.parse(stream)

            val nodeListModel = document.getElementsByTagName("Model")
            if (nodeListModel.length < 1) throw IllegalStateException("Tcn Model contains no Model tag")

            nodeListModel.item(0).attributes ?: throw IllegalStateException(
                    "Tcn Model contains a Model tag with no attributes")

            val textureDim = document.getElementsByTagName("TextureSize")
            var textureDims: Dimension? = null
            if (textureDim.length > 0) {
                try {
                    val tmp = textureDim.item(0).textContent.split(",".toRegex()).dropLastWhile(String::isEmpty)
                    if (tmp.size == 2) {
                        textureDims = Dimension(tmp[0].toInt(), tmp[1].toInt())
                    }
                } catch (e: NumberFormatException) {
                    throw IllegalStateException("Tcn Model contains a TextureSize tag with invalid data")
                }
            }
            if (textureDims != null && textureDims.height == textureDims.width) {
//                TextureLoader.textureScale = textureDims.getWidth()
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
                    meshes += getMesh(shape)
                } catch (e: NumberFormatException) {
                    log(Level.ERROR) { "Tcn file contains malformed integers within its data, ignoring" }
                    e.print()
                }
            }
            val groups = listOf(ModelGroup(meshes, name = "Group_1"))
            Model(listOf(ModelObject(groups, name = "Tcn_model", material = MaterialNone)))
        }
    }

    @Throws(NumberFormatException::class)
    private fun getMesh(shape: Node): Mesh {
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
                    "Offset" -> offset = shapeChildValue.split(",".toRegex()).dropLastWhile(String::isEmpty)
                    "Position" -> position = shapeChildValue.split(",".toRegex()).dropLastWhile(String::isEmpty)
                    "Rotation" -> rotation = shapeChildValue.split(",".toRegex()).dropLastWhile(String::isEmpty)
                    "Size" -> size = shapeChildValue.split(",".toRegex()).dropLastWhile(String::isEmpty)
                    "TextureOffset" -> textureOffset = shapeChildValue.split(",".toRegex()).dropLastWhile(
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

        val cube = Mesh.createCube(size = rSize, offset = fOffset)
        if (rRotation.lengthSq() == 0.0) return cube
        return cube.copy(cube.positions.map {
            rotatePointAroundPivot(it, rRotPoint, rRotation)
        })
    }
}