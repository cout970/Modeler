package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.to_redo.newView.TransformationMode
import com.cout970.modeler.util.toInt
import com.cout970.modeler.view.render.RenderContextOld
import com.cout970.vector.api.IVector3
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/20.
 */
class GridsRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContextOld) {
        val perspective = ctx.scene.perspective
        ctx.apply {
            val camera = scene.cameraHandler.camera

            draw(GL11.GL_LINES, shaderHandler.formatPC,
                    scene.viewTarget.selectedObject?.hashCode() ?: 0 xor 0xABCD xor perspective.toInt(), camera) {

                set(0, -10, 0, 0).set(1, 1, 0, 0).endVertex()
                set(0, 10, 0, 0).set(1, 1, 0, 0).endVertex()

                set(0, 0, -10, 0).set(1, 0, 1, 0).endVertex()
                set(0, 0, 10, 0).set(1, 0, 1, 0).endVertex()

                set(0, 0, 0, -10).set(1, 0, 0, 1).endVertex()
                set(0, 0, 0, 10).set(1, 0, 0, 1).endVertex()

                val dist = -1024 * 15
                val grid1 = Config.colorPalette.grid1Color
                val grid2 = Config.colorPalette.grid2Color
                var color: IVector3

                // ortho planes
                if (!perspective && camera.angleX % 90 == 0.0 || camera.angleY % 90 == 0.0) {
                    //y
                    for (x in -160..160) {
                        color = if (x % 16 == 0) grid2 else grid1
                        set(0, x, dist, -160).setVec(1, color).endVertex()
                        set(0, x, dist, 160).setVec(1, color).endVertex()
                    }
                    for (z in -160..160) {
                        color = if (z % 16 == 0) grid2 else grid1
                        set(0, -160, dist, z).setVec(1, color).endVertex()
                        set(0, 160, dist, z).setVec(1, color).endVertex()
                    }

                    //x
                    for (x in -160..160) {
                        color = if (x % 16 == 0) grid2 else grid1
                        set(0, dist, x, -160).setVec(1, color).endVertex()
                        set(0, dist, x, 160).setVec(1, color).endVertex()
                    }
                    for (z in -160..160) {
                        color = if (z % 16 == 0) grid2 else grid1
                        set(0, dist, -160, z).setVec(1, color).endVertex()
                        set(0, dist, 160, z).setVec(1, color).endVertex()
                    }

                    //z
                    for (z in -160..160) {
                        color = if (z % 16 == 0) grid2 else grid1
                        set(0, -160, z, dist).setVec(1, color).endVertex()
                        set(0, 160, z, dist).setVec(1, color).endVertex()
                    }
                    for (x in -160..160) {
                        color = if (x % 16 == 0) grid2 else grid1
                        set(0, x, -160, dist).setVec(1, color).endVertex()
                        set(0, x, 160, dist).setVec(1, color).endVertex()
                    }
                }

                if (scene.viewTarget.selectedObject == null || !perspective ||
                    controllerState.transformationMode != TransformationMode.TRANSLATION) {

                    color = Config.colorPalette.grid2Color

                    for (x in -7..8) {
                        set(0, x * 16, 0, -7 * 16).setVec(1, color).endVertex()
                        set(0, x * 16, 0, 8 * 16).setVec(1, color).endVertex()
                    }

                    for (z in -7..8) {
                        set(0, -7 * 16, 0, z * 16).setVec(1, color).endVertex()
                        set(0, 8 * 16, 0, z * 16).setVec(1, color).endVertex()
                    }
                }
            }
        }
    }
}