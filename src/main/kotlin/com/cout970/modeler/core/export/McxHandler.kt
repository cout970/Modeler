package com.cout970.modeler.core.export

import com.cout970.modeler.api.model.IModel
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
            .registerTypeAdapter(QuadIndices::class.java, QuadIndicesSerializer())
            .setPrettyPrinting()
            .create()!!

    fun export(output: OutputStream, model: IModel, domain: String) {

        val posSet = mutableSetOf<IVector3>()
        val texSet = mutableSetOf<IVector2>()

        model.objects.forEach {
            posSet += it.mesh.pos
            texSet += it.mesh.tex
        }

        val pos = posSet.toList()
        val tex = texSet.toList()
        val indices = mutableListOf<QuadIndices>()
        val parts = mutableListOf<Part>()
        var particleTexture: String? = null

        model.objects.forEach { obj ->
            val texture = "$domain:${model.getMaterial(obj.material).name}"
            val mesh = obj.mesh
            val localIndices = mesh.faces.map { face ->
                val (ap, bp, cp, dp) = face.pos
                val (at, bt, ct, dt) = face.tex
                QuadIndices(
                        pos.indexOf(mesh.pos[ap]),
                        pos.indexOf(mesh.pos[bp]),
                        pos.indexOf(mesh.pos[cp]),
                        pos.indexOf(mesh.pos[dp]),
                        tex.indexOf(mesh.tex[at]),
                        tex.indexOf(mesh.tex[bt]),
                        tex.indexOf(mesh.tex[ct]),
                        tex.indexOf(mesh.tex[dt])
                )
            }
            if (particleTexture == null) particleTexture = texture

            parts += Part(obj.name, indices.size, indices.size + localIndices.size, null, texture)
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

    data class ModelData(
            val useAmbientOcclusion: Boolean,
            val use3dInGui: Boolean,
            val particleTexture: String,
            val parts: List<Part>,
            val quads: QuadStorage
    )

    data class Part(val name: String, val from: Int, val to: Int, val side: Direction?, val texture: String)

    class QuadStorage(val pos: List<IVector3>, val tex: List<IVector2>, val indices: List<QuadIndices>) {

        override fun toString(): String {
            return "QuadStorage(pos=[${pos.size}], tex=[${tex.size}], indices=[${indices.size}])"
        }
    }

    class QuadIndices(val a: Int, val b: Int, val c: Int, val d: Int,
                      val at: Int, val bt: Int, val ct: Int, val dt: Int)
}