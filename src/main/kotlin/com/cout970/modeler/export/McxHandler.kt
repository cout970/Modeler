package com.cout970.modeler.export

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.getObjectElements
import com.cout970.modeler.model.zipGroups
import com.cout970.modeler.util.Direction
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.times
import com.google.gson.GsonBuilder
import java.io.OutputStream

/**
 * Created by cout970 on 2017/01/26.
 */

class McxExporter {

    val GSON = GsonBuilder()
            .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
            .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
            .registerTypeAdapter(QuadStorage.QuadIndices::class.java, QuadIndicesSerializer())
            .setPrettyPrinting()
            .create()!!

    fun export(output: OutputStream, model: Model, domain: String) {

        val posSet = mutableSetOf<IVector3>()
        val texSet = mutableSetOf<IVector2>()

        model.getObjectElements().forEach {
            posSet += it.positions
            texSet += it.textures
        }

        val pos = posSet.toList()
        val tex = texSet.toList()
        val indices = mutableListOf<QuadStorage.QuadIndices>()
        val parts = mutableListOf<ModelData.Part>()
        var particleTexture: String? = null

        model.zipGroups()
        //TODO redo format
//        for (group in model.groups) {
//            val texture = "$domain:${group.material.name}"
//            val localIndices = mutableListOf<QuadStorage.QuadIndices>()
//            for (mesh in group.meshes) {
//                localIndices += mesh.indices.map { i ->
//                    QuadStorage.QuadIndices(
//                            pos.indexOf(group.transform.matrix * mesh.positions[i.aP]),
//                            pos.indexOf(group.transform.matrix * mesh.positions[i.bP]),
//                            pos.indexOf(group.transform.matrix * mesh.positions[i.cP]),
//                            pos.indexOf(group.transform.matrix * mesh.positions[i.dP]),
//                            tex.indexOf(mesh.textures[i.aT]),
//                            tex.indexOf(mesh.textures[i.bT]),
//                            tex.indexOf(mesh.textures[i.cT]),
//                            tex.indexOf(mesh.textures[i.dT])
//                    )
//                }
//            }
//            if (particleTexture == null) particleTexture = texture
//            parts += ModelData.Part(indices.size, indices.size + localIndices.size, null, texture)
//            indices += localIndices
//        }

        val data = ModelData(
                useAmbientOcclusion = true,
                use3dInGui = true,
                particleTexture = particleTexture ?: "minecraft:missigno",
                parts = parts,
                quads = QuadStorage(pos.map { it * (1 / 16.0) }, tex, indices)
        )
        val str = GSON.toJson(data)
        output.write(str.toByteArray())
        output.close()
    }

    class ModelData(
            val useAmbientOcclusion: Boolean,
            val use3dInGui: Boolean,
            val particleTexture: String,
            val parts: List<Part>,
            val quads: QuadStorage
    ) {

        class Part(val from: Int, val to: Int, val side: Direction?, val texture: String)
    }

    class QuadStorage(val pos: List<IVector3>, val tex: List<IVector2>, val indices: List<QuadIndices>) {

        class QuadIndices(val a: Int, val b: Int, val c: Int, val d: Int,
                          val at: Int, val bt: Int, val ct: Int, val dt: Int)

    }
}