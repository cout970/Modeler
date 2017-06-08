package com.cout970.modeler.view.render.control

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.controller.World
import com.cout970.modeler.core.export.TcnImporter
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.core.resource.toResourcePath
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11
import java.io.File

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager, val input: IInput) {

    fun render(state: GuiState) {

        state.canvasContainer.canvas.forEach { canvas ->
            val ctx = RenderContext(
                    camera = canvas.cameraHandler.camera,
                    input = input,
                    lights = canvas.lights,
                    viewport = canvas.size.toIVector(),
                    windowHandler = state.windowHandler,
                    timer = state.timer,
                    shader = renderManager.shader
            )
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    state.windowHandler.window.size.yf - (canvas.absolutePosition.yf + canvas.size.y)
            )
            state.windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                renderCanvas(ctx)
            }
        }
    }

    val model = TcnImporter().import(File("I:/newWorkspace/Proyectos/Java_Kotlin/Modeler/run/electric_sieve.tcn")
            .toResourcePath())

    val world = World(listOf(model))
//    model.copy(objects = model.objects.map {
//        it.transform { it.transform(TRSTransformation(scale = vec3Of(3, 1, 3))) }
//    })))

    fun renderCanvas(ctx: RenderContext) {

        renderManager.shader.useShader(ctx) { buffer, shader ->
            world.models.forEachIndexed { modelIndex, model ->

                if (world.cache[modelIndex].size != model.objects.size) {
                    world.cache[modelIndex].forEach { it.close() }
                    world.cache[modelIndex] = updateCache(buffer, model)
                    model.objects.forEach { it.material.loadTexture(ResourceLoader()) }
                }
                model.objects
                        .mapIndexed { index, iObject -> index to iObject }
                        .groupBy { it.second.material }
                        .forEach { material, list ->
                            material.bind()
                            list.forEach { (index, obj) ->
                                shader.useTexture.setInt(1)
                                shader.useColor.setInt(0)
                                shader.useLight.setInt(1)
                                shader.matrixM.setMatrix4(obj.transformation.matrix)
                                shader.accept(world.cache[modelIndex][index])
                            }
                        }
            }
        }
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
