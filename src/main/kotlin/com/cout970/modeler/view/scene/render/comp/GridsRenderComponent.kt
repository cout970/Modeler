package com.cout970.modeler.view.scene.render.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.scene.render.RenderContext
import com.cout970.vector.api.IVector3
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/20.
 */
class GridsRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        val perspective = ctx.scene.perspective
        ctx.apply {
            draw(GL11.GL_LINES, shaderHandler.formatPC) {
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

                val selX = sceneController.selectedModelAxis == SelectionAxis.X
                val selY = sceneController.selectedModelAxis == SelectionAxis.Y
                val selZ = sceneController.selectedModelAxis == SelectionAxis.Z

                if (!selX && !selY && !selZ || !perspective ||
                    sceneController.transformationMode != TransformationMode.TRANSLATION) {

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