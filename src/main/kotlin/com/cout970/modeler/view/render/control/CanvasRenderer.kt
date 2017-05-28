package com.cout970.modeler.view.render.control

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.TcnImporter
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.MeshFactory
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

    val model = TcnImporter().import(
            File("I:/newWorkspace/Proyectos/Java_Kotlin/Modeler/run/electric_sieve.tcn").toResourcePath()).run {
        val newObject = Object("", transformation = TRSTransformation.IDENTITY,
                mesh = MeshFactory.createPlane(vec2Of(256, 256)))
        copy(objects + newObject)
    }
    val material = TexturedMaterial("modelTexture",
            File("I:/newWorkspace/Proyectos/Java_Kotlin/Modeler/run/electric_sieve.png").toResourcePath())

    var cache = listOf<VAO>()

    fun renderCanvas(ctx: RenderContext) {

        renderManager.shader.useShader(ctx) { buffer, shader ->
            if (cache.size != model.objects.size) {
                cache.forEach { it.close() }
                cache = updateCache(buffer, model)
                material.loadTexture(ResourceLoader())
            }
            material.bind()
            model.objects.forEachIndexed { index, obj ->
                shader.useTexture.setInt(0)
                shader.useColor.setInt(1)
                shader.useLight.setInt(1)
                shader.matrixM.setMatrix4(obj.transformation.matrix)
                shader.accept(cache[index])
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
