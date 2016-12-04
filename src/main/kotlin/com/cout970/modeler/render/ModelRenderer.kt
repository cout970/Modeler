package com.cout970.modeler.render

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.glutilities.tessellator.VAO
import com.cout970.glutilities.tessellator.format.FormatPT
import com.cout970.matrix.extensions.mat4Of
import com.cout970.modeler.Init
import com.cout970.modeler.ModelController
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.model.Quad
import com.cout970.modeler.util.Cache
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.joml.Vector3d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.util.*
import java.util.function.Consumer

/**
 * Created by cout970 on 2016/12/03.
 */
class ModelRenderer(
        resourceManager: ResourceManager
) {
    var consumer: Consumer<VAO>
    var shader: ShaderProgram
    val tessellator = Tessellator()
    val color: UniformVariable
    val panelSize: UniformVariable
    val matrixMVP: UniformVariable
    var x = 0f
    var y = 0f
    var offsetX = 0f
    var offsetY = 0f
    var offsetZ = 0f
    var zoom = -10f
    var zoom2 = 0f
    val cache = Cache<Int, VAO>(2)

    fun debug(i: Init) {
        i.eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.RELEASE) return false
                if (e.keycode == Keyboard.KEY_UP) {
                    x += 0.05f
                } else if (e.keycode == Keyboard.KEY_DOWN) {
                    x -= 0.05f
                }

                if (e.keycode == Keyboard.KEY_LEFT) {
                    y += 0.05f
                } else if (e.keycode == Keyboard.KEY_RIGHT) {
                    y -= 0.05f
                }

                if (e.keycode == Keyboard.KEY_Q) {
                    zoom += 0.5f
                } else if (e.keycode == Keyboard.KEY_E) {
                    zoom -= 0.5f
                }

                if (e.keycode == Keyboard.KEY_R) {
                    zoom2 += 100f
                } else if (e.keycode == Keyboard.KEY_T) {
                    zoom2 -= 100f
                }

                if (e.keycode == Keyboard.KEY_D) {
                    offsetX += 0.05f
                } else if (e.keycode == Keyboard.KEY_A) {
                    offsetX -= 0.05f
                }

                if (e.keycode == Keyboard.KEY_S) {
                    offsetZ += 0.05f
                } else if (e.keycode == Keyboard.KEY_W) {
                    offsetZ -= 0.05f
                }
                if (e.keycode == Keyboard.KEY_G) {
                    offsetY += 0.05f
                } else if (e.keycode == Keyboard.KEY_H) {
                    offsetY -= 0.05f
                }

                if (e.keycode == Keyboard.KEY_P) {
                    cache.clear()
                }
                return false
            }
        })
    }

    init {
        shader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER, resourceManager.readResource("shaders/model_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER, resourceManager.readResource("shaders/model_fragment.glsl").reader().readText())
            bindAttribute(0, "pos")
            bindAttribute(1, "tex")
        }

        color = shader.createUniformVariable("color")
        panelSize = shader.createUniformVariable("panelSize")
        matrixMVP = shader.createUniformVariable("matrixMVP")

        consumer = Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.unbind()
        }

    }

    fun render(modelController: ModelController, pos: IVector2, size: IVector2) {
        GL11.glViewport(pos.xi, pos.yi, size.xi, size.yi)
        shader.start()
        panelSize.setVector2(size)

        val matrix = Matrix4d().setPerspective(Math.toRadians(60.0), size.xd / size.yd, 0.001, 1000.0)
                .mul(Matrix4d().apply {
                    translate(Vector3d(0.0, 0.0, zoom.toDouble()))
                    rotate(x.toDouble(), Vector3d(1.0, 0.0, 0.0))
                    rotate(y.toDouble(), Vector3d(0.0, 1.0, 0.0))
                }).run {
            mat4Of(m00(), m01(), m02(), m03(),
                    m10(), m11(), m12(), m13(),
                    m20(), m21(), m22(), m23(),
                    m30(), m31(), m32(), m33())
        }
        matrixMVP.setMatrix4(matrix)

//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)

        consumer.accept(cache.getOrCompute(0) {
            val rand = Random()
            color.setVector3(vec3Of(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()))
            tessellator.compile(GL11.GL_QUADS, FormatPT()) {
                modelController.model.getComponents().forEach {
                    it.getQuads().map(Quad::vertex).forEach {
                        it.forEach {
                            set(0, it.pos.xd, it.pos.yd, it.pos.zd).set(1, it.tex.x, it.tex.y).endVertex()
                        }
                    }
                }
            }
        })
//            color.setVector3(vec3Of(1, 0, 0))
//            tessellator.draw(GL11.GL_LINES, FormatPT(), consumer) {
//                set(0, -1000, 0, 0).set(1, 1, 0).endVertex()
//                set(0, 1000, 0, 0).set(1, 1, 0).endVertex()
//            }
////        tessellator.draw(GL11.GL_POINTS, FormatPT(), consumer) {
////            for (i in -100..100)
////                set(0, i, 0, 0).set(1, 0, 1).endVertex()
////        }
//
//            color.setVector3(vec3Of(0, 1, 0))
//            tessellator.draw(GL11.GL_LINES, FormatPT(), consumer) {
//                set(0, 0, -1000, 0).set(1, 0, 1).endVertex()
//                set(0, 0, 1000, 0).set(1, 0, 1).endVertex()
//            }
////        tessellator.draw(GL11.GL_POINTS, FormatPT(), consumer) {
////            for (i in -100..100)
////                set(0, 0, i, 0).set(1, 0, 1).endVertex()
////        }
//
//            color.setVector3(vec3Of(0, 0, 1))
//            tessellator.draw(GL11.GL_LINES, FormatPT(), consumer) {
//                set(0, 0, 0, -1000).set(1, 0, 1).endVertex()
//                set(0, 0, 0, 1000).set(1, 1, 0).endVertex()
//            }
////        tessellator.draw(GL11.GL_POINTS, FormatPT(), consumer) {
////            for (i in -100..100)
////                set(0, 0, 0, i).set(1, 0, 1).endVertex()
////        }
////        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        shader.stop()
    }
}