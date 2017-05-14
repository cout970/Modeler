package com.cout970.modeler.core.export

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.api.IElementLeaf
import com.cout970.modeler.to_redo.model.util.getLeafElements
import com.cout970.modeler.to_redo.model.util.zipGroups
import com.cout970.modeler.util.Direction
import com.cout970.modeler.util.castTo
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

        model.getLeafElements().forEach {
            posSet += it.positions
            texSet += it.textures
        }

        val pos = posSet.toList()
        val tex = texSet.toList()
        val indices = mutableListOf<QuadStorage.QuadIndices>()
        val parts = mutableListOf<ModelData.Part>()
        var particleTexture: String? = null

        for ((name, elements) in model.zipGroups()) {
            val texture = "$domain:${model.resources.materials[0].name}"
            val localIndices = mutableListOf<QuadStorage.QuadIndices>()
            for (mesh in elements.castTo<IElementLeaf>()) {
                localIndices += mesh.faces.map { (a, b, c, d) ->
                    QuadStorage.QuadIndices(
                            pos.indexOf(mesh.positions[a.pos]),
                            pos.indexOf(mesh.positions[b.pos]),
                            pos.indexOf(mesh.positions[c.pos]),
                            pos.indexOf(mesh.positions[d.pos]),
                            tex.indexOf(mesh.textures[a.tex]),
                            tex.indexOf(mesh.textures[b.tex]),
                            tex.indexOf(mesh.textures[c.tex]),
                            tex.indexOf(mesh.textures[d.tex])
                    )
                }
            }
            if (particleTexture == null) particleTexture = texture
            parts += ModelData.Part(name, indices.size, indices.size + localIndices.size, null, texture)
            indices += localIndices
        }

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

        class Part(val name: String, val from: Int, val to: Int, val side: Direction?, val texture: String)
    }

    class QuadStorage(val pos: List<IVector3>, val tex: List<IVector2>, val indices: List<QuadIndices>) {

        class QuadIndices(val a: Int, val b: Int, val c: Int, val d: Int,
                          val at: Int, val bt: Int, val ct: Int, val dt: Int)

    }
}