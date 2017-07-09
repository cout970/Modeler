package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.util.quatOfAngles
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by cout970 on 2017/01/12.
 */

class TcnImporter {

    val cubeTypes = listOf("d9e621f7-957f-4b77-b1ae-20dcd0da7751", "de81aa14-bd60-4228-8d8d-5238bcd3caaa")
    val COMMA = ",".toRegex()

    fun import(path: ResourcePath): IModel {
        val model = path.enterZip("model.xml")
        val stream = model.inputStream()
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(stream)

        var texture: IMaterial = MaterialNone
        val nodeListModel = document.getElementsByTagName("Model")
        if (nodeListModel.length < 1) throw IllegalStateException(
                "Tcn Model contains no Model tag, (no models in the file)")

        nodeListModel.item(0).attributes ?: throw IllegalStateException(
                "Tcn Model contains a Model tag with no attributes")

        val textureName = nodeListModel.item(0).attributes.getNamedItem("texture")
        if (textureName != null) {
            texture = TexturedMaterial(textureName.textContent,
                    path.enterZip(textureName.textContent))
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
        val meshes = mutableListOf<IObjectCube>()

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
                meshes += getMesh(shape, textureSize, MaterialRef(0))
            } catch (e: NumberFormatException) {
                log(Level.ERROR) { "Tcn file contains malformed integers within its data, ignoring" }
                e.print()
            }
        }
        return Model.of(meshes, listOf(texture))
    }

    @Throws(NumberFormatException::class)
    private fun getMesh(shape: Node, textureSize: IVector2, texture: IMaterialRef): IObjectCube {
        var mirrored = false
        var offset: List<String> = listOf()
        var position: List<String> = listOf()
        var rotation: List<String> = listOf()
        var size: List<String> = listOf()
        var textureOffset: List<String> = listOf()
        val shapeName = shape.attributes.getNamedItem("name").textContent

        val shapeChildren = shape.childNodes
        //extract properties
        for (j in 0..shapeChildren.length - 1) {

            val shapeChild = shapeChildren.item(j)
            val name = shapeChild.nodeName
            var value = shapeChild.textContent

            if (value != null) {
                value = value.trim { it <= ' ' }

                when (name) {
                    "IsMirrored" -> mirrored = value != "False"
                    "Offset" -> offset = value.split(COMMA).dropLastWhile(String::isEmpty)
                    "Position" -> position = value.split(COMMA).dropLastWhile(String::isEmpty)
                    "Rotation" -> rotation = value.split(COMMA).dropLastWhile(String::isEmpty)
                    "Size" -> size = value.split(COMMA).dropLastWhile(String::isEmpty)
                    "TextureOffset" -> textureOffset = value.split(COMMA).dropLastWhile(String::isEmpty)
                }
            }
        }

        val vSize = vec3Of(size[0].toInt(), size[1].toInt(), size[2].toInt())
        val vOffset = vec3Of(offset[0].toFloat(), offset[1].toFloat(), offset[2].toFloat())
        val vPos = vec3Of(position[0].toFloat(), position[1].toFloat(), position[2].toFloat())
        val vTexture = vec2Of(textureOffset[0].toInt(), textureOffset[1].toInt())
        val vRotation = vec3Of(rotation[0].toFloat(), rotation[1].toFloat(), rotation[2].toFloat())

        val rSize = vSize
        val rOffset = vOffset * vec3Of(1, -1, -1)
        val rPos = vPos * vec3Of(1, -1, -1) - vec3Of(0, vSize.y, vSize.z)
        val rTexture = vTexture
        val rRotation = vRotation.toRadians() * vec3Of(1, 1, -1)
        val rRotPoint = vPos * vec3Of(1, -1, -1) + vec3Of(8, 24, 8)

        val fOffset = rPos + rOffset + vec3Of(8, 24, 8)

        val cube = ObjectCube(
                name = shapeName,
                size = rSize,
                pos = fOffset,
                rotation = quatOfAngles(rRotation),
                rotationPivot = rRotPoint,
                transformation = TRSTransformation.IDENTITY,
                textureOffset = rTexture,
                textureSize = textureSize,
                mirrored = mirrored,
                material = texture
        )
        return cube
    }
}