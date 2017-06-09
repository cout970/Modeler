package com.cout970.modeler.view.render.control

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.controller.World
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.vector.extensions.cross
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.normalize
import com.cout970.vector.extensions.transform
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/09.
 */

class ModelRenderer {

    fun render(ctx: RenderContext, buffer: UniversalShader.Buffer, world: World) {
        val cache = world.cache

        world.models.forEachIndexed { modelIndex, model ->

            if (cache[modelIndex].size != model.objects.size) {
                cache[modelIndex] = buildCache(cache[modelIndex], buffer, model)
            }

            val map = model.objects
                    .mapIndexed { index, iObject -> index to iObject }
                    .groupBy { it.second.material }

            map.forEach { material, list ->
                material.bind()
                list.forEach { (index, obj) ->
                    ctx.shader.apply {
                        useTexture.setInt(1)
                        useColor.setInt(0)
                        useLight.setInt(1)
                        matrixM.setMatrix4(obj.transformation.matrix)
                        accept(cache[modelIndex][index])
                    }
                }
            }
        }
    }

    private fun buildCache(list: List<VAO>, buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        list.forEach { it.close() }
        model.objects.forEach { it.material.loadTexture(ResourceLoader()) }
        return updateCache(buffer, model)
    }

    private fun updateCache(buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        return model.objects
                .map { it.mesh }
                .map { buildMeshVao(buffer, it) }
    }

    fun buildMeshVao(buffer: UniversalShader.Buffer, mesh: IMesh): VAO {
        return buffer.build(GL11.GL_QUADS) {
            mesh.faces.forEach { face ->

                val (a, b, c, d) = face.pos.map { mesh.pos[it] }
                val ac = c - a
                val bd = d - b
                val normal = (ac cross bd).normalize()

                for (index in 0 until face.vertexCount) {
                    add(mesh.pos[face.pos[index]], mesh.tex[face.tex[index]],
                            vnorm = normal,
                            vcol = normal.transform { Math.abs(it) }
                    )
                }
            }
        }
    }
}