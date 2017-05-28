package com.cout970.modeler.view.render.control

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager, val input: IInput) {

    fun render(state: GuiState) {


        state.canvasContainer.canvas.forEach { canvas ->
            val ctx = RenderContext(
                    camera = canvas.state.cameraHandler.camera,
                    input = input,
                    lights = canvas.state.lights,
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

    val model = Model(objects = listOf(
            ObjectCube(
                    name = "Test", pos = Vector3.ORIGIN, size = Vector3.ONE * 16, rotation = Quaternion.IDENTITY,
                    transformation = TRSTransformation(
                            translation = Vector3.ONE * 16,
                            rotation = quatOfAngles(-45.toRads(), 0, 0),
                            scale = Vector3.ONE
                    )),
            ObjectCube(
                    name = "Test1", pos = Vector3.ORIGIN, size = Vector3.ONE * 16, rotation = Quaternion.IDENTITY,
                    transformation = TRSTransformation(
                            translation = Vector3.ORIGIN,
                            rotation = Quaternion.IDENTITY,
                            scale = Vector3.ONE
                    ))
    ))
    var cache = listOf<VAO>()

    fun renderCanvas(ctx: RenderContext) {

        renderManager.shader.useShader(ctx) { buffer, consumer ->

            if (cache.size != model.objects.size) {
                cache.forEach { it.close() }
                cache = updateCache(buffer, model)
            }
            model.objects.forEachIndexed { index, obj ->
                consumer.matrixM.setMatrix4(obj.transformation.matrix)
                consumer.accept(cache[index])
            }
        }
    }

    private fun updateCache(buffer: UniversalShader.Buffer, model: Model): List<VAO> {
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
