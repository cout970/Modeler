package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.to_redo.newView.TransformationMode
import com.cout970.modeler.to_redo.newView.selector.*
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.getDominantAxis
import com.cout970.modeler.view.gui.camera.Camera
import com.cout970.modeler.view.render.RenderContextOld
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/04/03.
 */
object CursorRenderer {

    fun RenderContextOld.drawCursor(cursor: Cursor, camera: Camera, allowGrids: Boolean) {
        if (!cursor.enable) return
        when (cursor.transformationMode) {
            TransformationMode.TRANSLATION -> {

                if (allowGrids && Config.enableHelperGrid && scene.perspective) {
                    drawHelperGrids(this, cursor, camera)
                }
                renderTranslation(this, cursor)
            }
            TransformationMode.ROTATION -> {
                renderRotation(this, cursor)
            }
            TransformationMode.SCALE -> {

                if (allowGrids && Config.enableHelperGrid && scene.perspective) {
                    drawHelperGrids(this, cursor, camera)
                }
                renderScale(this, cursor)
            }
        }
    }

    fun renderTranslation(ctx: RenderContextOld, cursor: Cursor) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        ctx.apply {
            draw(GL11.GL_QUADS, shaderHandler.formatPC) {

                val center = cursor.center
                val radius = cursor.getCursorParameters(scene).distanceFromCenter
                val start = radius - cursor.getCursorParameters(scene).maxSizeOfSelectionBox / 2.0
                val end = radius + cursor.getCursorParameters(scene).maxSizeOfSelectionBox / 2.0

                for (part in cursor.getSubParts(scene)) {
                    (part as? CursorPartTranslate)?.let { part ->
                        val selected = ctx.scene.viewTarget.selectedObject == part ||
                                       ctx.scene.viewTarget.hoveredObject == part

                        RenderUtil.renderBar(
                                tessellator = this,
                                startPoint = center + part.translationAxis * start,
                                endPoint = center + part.translationAxis * end,
                                color = part.color,
                                size = if (selected)
                                    cursor.getCursorParameters(scene).minSizeOfSelectionBox * 1.5
                                else
                                    cursor.getCursorParameters(scene).minSizeOfSelectionBox
                        )
                    }
                }
            }
        }
    }

    fun renderRotation(ctx: RenderContextOld, cursor: Cursor) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        ctx.apply {
            draw(GL11.GL_QUADS, shaderHandler.formatPC) {

                //if one of the axis is selected
                if (scene.viewTarget.selectedObject != null) {

                    (scene.viewTarget.selectedObject as? CursorPartRotation)?.let { part ->
                        RenderUtil.renderCircle(
                                t = this,
                                center = cursor.center,
                                axis = part.normal,
                                radius = cursor.getCursorParameters(scene).distanceFromCenter,
                                size = Config.cursorLinesSize * cursor.getCursorParameters(scene).minSizeOfSelectionBox,
                                color = part.color
                        )
                    }
                } else {
                    for (part in cursor.getSubParts(scene)) {
                        (part as? CursorPartRotation)?.let { part ->

                            RenderUtil.renderCircle(
                                    t = this,
                                    center = cursor.center,
                                    axis = part.normal,
                                    radius = cursor.getCursorParameters(scene).distanceFromCenter,
                                    size = Config.cursorLinesSize * cursor.getCursorParameters(
                                            scene).minSizeOfSelectionBox,
                                    color = part.color
                            )
                        }
                    }

                    val radius = cursor.getCursorParameters(scene).distanceFromCenter

                    for (part in cursor.getSubParts(scene)) {
                        (part as? CursorPartRotation)?.let { part ->

                            val edgePoint = cursor.center + part.axis * radius
                            val selected = scene.viewTarget.selectedObject == part ||
                                           scene.viewTarget.hoveredObject == part

                            RenderUtil.renderBar(
                                    tessellator = this,
                                    startPoint = edgePoint - part.coaxis * cursor.getCursorParameters(
                                            scene).maxSizeOfSelectionBox / 2,
                                    endPoint = edgePoint + part.coaxis * cursor.getCursorParameters(
                                            scene).maxSizeOfSelectionBox / 2,
                                    color = vec3Of(1),
                                    size = if (selected) {
                                        cursor.getCursorParameters(scene).minSizeOfSelectionBox * 1.5
                                    } else {
                                        cursor.getCursorParameters(scene).minSizeOfSelectionBox
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    fun renderScale(ctx: RenderContextOld, cursor: Cursor) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        ctx.apply {
            draw(GL11.GL_QUADS, shaderHandler.formatPC) {

                val center = cursor.center
                val radius = cursor.getCursorParameters(scene).distanceFromCenter
                val start = radius - cursor.getCursorParameters(scene).maxSizeOfSelectionBox / 2.0
                val end = radius + cursor.getCursorParameters(scene).maxSizeOfSelectionBox / 2.0

                for (part in cursor.getSubParts(scene)) {
                    (part as? CursorPartScale)?.let { part ->
                        val selected = ctx.scene.viewTarget.selectedObject == part ||
                                       ctx.scene.viewTarget.hoveredObject == part

                        RenderUtil.renderBar(
                                tessellator = this,
                                startPoint = center + part.scaleAxis * start,
                                endPoint = center + part.scaleAxis * end,
                                color = part.color,
                                size = if (selected)
                                    cursor.getCursorParameters(scene).minSizeOfSelectionBox * 1.5
                                else
                                    cursor.getCursorParameters(scene).minSizeOfSelectionBox
                        )
                    }
                }
            }
        }
    }

    // 0 -> X
    // 1 -> Y
    // 2 -> Z
    private fun extractAxis(obj: ISelectable): Int {
        return when (obj) {
            is ITranslatable -> obj.translationAxis.getDominantAxis()
            is IRotable -> obj.normal.getDominantAxis()
            is IScalable -> obj.scaleAxis.getDominantAxis()
            else -> 0
        }
    }

    private fun drawHelperGrids(ctx: RenderContextOld, cursor: Cursor, camera: Camera) {
        ctx.apply {
            val sel = ctx.scene.viewTarget.selectedObject
            if (sel != null) {
                draw(GL11.GL_LINES, shaderHandler.formatPC) {
                    val grid1 = Config.colorPalette.grid1Color
                    val grid2 = Config.colorPalette.grid2Color
                    var col: IVector3
                    val center = cursor.center



                    if (extractAxis(sel) != 1) {
                        for (x in -160..160) {
                            col = if (x % 16 == 0) grid2 else grid1
                            set(0, x, center.y, -160).set(1, col.x, col.y, col.z).endVertex()
                            set(0, x, center.y, 160).set(1, col.x, col.y, col.z).endVertex()
                        }
                        for (z in -160..160) {
                            col = if (z % 16 == 0) grid2 else grid1
                            set(0, -160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                            set(0, 160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                        }
                    } else {

                        //test
                        val vec0 = Vector3.X_AXIS.rotate(-camera.angleY, Vector3.Y_AXIS).normalize()
                        val vec1 = Vector3.X_AXIS.rotate(camera.angleY, Vector3.Y_AXIS).normalize()

                        for (i in -160..160) {
                            col = if (i % 16 == 0) grid2 else grid1
                            set(0, vec0.xf * -160f + center.xf, i, vec0.zf * -160f + center.zf)
                                    .set(1, col.x, col.y, col.z)
                                    .endVertex()
                            set(0, vec0.xf * 160f + center.xf, i, vec0.zf * 160f + center.zf)
                                    .set(1, col.x, col.y, col.z)
                                    .endVertex()
                        }
                        for (i in -160..160) {
                            col = if (i % 16 == 0) grid2 else grid1
                            set(0, vec1.xf * i + center.xf, -160, vec1.zf * -i + center.zf)
                                    .set(1, col.x, col.y, col.z)
                                    .endVertex()
                            set(0, vec1.xf * i + center.xf, 160, vec1.zf * -i + center.zf)
                                    .set(1, col.x, col.y, col.z)
                                    .endVertex()
                        }
                    }
                }
            }
        }
    }
}