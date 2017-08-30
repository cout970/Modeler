package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import com.google.gson.GsonBuilder

/**
 * Created by cout970 on 2017/06/08.
 */

class TblImporter {

    companion object {
        @JvmStatic
        val GSON = GsonBuilder()
                .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
                .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
                .create()!!
        @JvmStatic
        val CENTER_OFFSET = vec3Of(8, 24, 8)
    }


    fun import(path: ResourcePath): IModel {

        val model = parse(path)
        val material = TexturedMaterial("texture", path.enterZip("texture.png"))
        val materialRef = MaterialRef(0)
        val texSize = vec2Of(model.textureWidth, model.textureHeight)

        val objects = mapCubes(model.cubes, materialRef, texSize) + mapGroups(model.cubeGroups, materialRef, texSize)
        return Model.of(objects, listOf(material))
    }

    fun mapGroups(list: List<CubeGroup>, material: IMaterialRef, texSize: IVector2): List<ObjectCube> {
        return list.flatMap {
            mapCubes(it.cubes, material, texSize) + mapGroups(it.cubeGroups, material, texSize)
        }
    }

    fun mapCubes(list: List<Cube>, material: IMaterialRef, texSize: IVector2): List<ObjectCube> {
        return list.map { cube ->
            ObjectCube(
                    name = cube.name,
                    pos = transformPos(cube),
                    subTransformation = TRTSTransformation.fromRotationPivot(
                            cube.position * vec3Of(1, -1, -1) + CENTER_OFFSET,
                            cube.rotation
                    ),
                    size = cube.dimensions,
                    material = material,
                    textureOffset = cube.txOffset,
                    textureSize = texSize,
                    mirrored = cube.txMirror
            )
        }
    }

    private fun transformPos(cube: Cube): IVector3 {
        val absPos = (cube.position + cube.offset) * vec3Of(1, -1, -1)
        return absPos - cube.dimensions * vec3Of(0, 1, 1) + CENTER_OFFSET
    }

    fun parse(path: ResourcePath): TblModel {
        val modelPath = path.enterZip("model.json")
        val stream = modelPath.inputStream()
        return GSON.fromJson(stream.reader(), TblModel::class.java)
    }

    data class TblModel(
            val modelName: String,
            val authorName: String,
            val projVersion: Int,
            val metadata: List<Any>,
            val textureWidth: Int,
            val textureHeight: Int,
            val scale: IVector3,

            val cubeGroups: List<CubeGroup>,
            val cubes: List<Cube>,

            val anims: List<Any>,
            val cubeCount: Int
    ) {
        override fun toString(): String {
            return "TblModel(\n" +
                   "    modelName='$modelName',\n" +
                   "    authorName='$authorName',\n" +
                   "    projVersion=$projVersion,\n" +
                   "    metadata=$metadata,\n" +
                   "    textureWidth=$textureWidth,\n" +
                   "    textureHeight=$textureHeight,\n" +
                   "    scale=$scale,\n" +
                   "    cubeGroups=[... size:${cubeGroups.size}],\n" +
                   "    cubes=[... size:${cubes.size}],\n" +
                   "    anims=$anims,\n" +
                   "    cubeCount=$cubeCount\n" +
                   ")"
        }
    }

    data class CubeGroup(
            val cubes: List<Cube>,
            val cubeGroups: List<CubeGroup>,
            val name: String,
            val txMirror: Boolean,
            val hidden: Boolean,
            val metadata: List<Any>,
            val identifier: String
    )

    data class Cube(
            val name: String,
            val dimensions: IVector3,
            val position: IVector3,
            val offset: IVector3,
            val rotation: IVector3,
            val scale: IVector3,
            val txOffset: IVector2,
            val txMirror: Boolean,
            val mcScale: Int,
            val opacity: Int,
            val hidden: Boolean,
            val metadata: List<Any>,
            val children: List<Any>,
            val identifier: String
    )
}