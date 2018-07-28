package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.render.tool.camera.Camera
import com.cout970.modeler.util.hasNaN
import com.cout970.modeler.util.middle
import com.cout970.modeler.util.toRads
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*

class Cursor2DTransformHelper {

    var modelCache: IModel? = null
    var offsetCache: IVector2 = Vector2.ORIGIN

    fun getTranslationOffset(gui: Gui, mouse: Pair<IVector2, IVector2>, canvas: Canvas, material: IMaterial): IVector2 {
        val start = PickupHelper.getMousePosAbsolute(canvas, mouse.first)
        val end = PickupHelper.getMousePosAbsolute(canvas, mouse.second)

        val canvasDiff = end - start

        val offset = when {
            Config.keyBindings.disableGridMotion.check(gui.input) -> (canvasDiff * 16).round() / 16f
            Config.keyBindings.disablePixelGridMotion.check(gui.input) -> (canvasDiff * 4).round() / 4f
            else -> canvasDiff.round()
        }

        val scaled = offset / material.size
        return vec2Of(scaled.xd, -scaled.yd)
    }

    fun getRotationOffset(gui: Gui, center: IVector2, mouse: Pair<IVector2, IVector2>, canvas: Canvas): Float {
        val start = PickupHelper.getMousePosAbsolute(canvas, mouse.first)
        val end = PickupHelper.getMousePosAbsolute(canvas, mouse.second)

        val angle1 = (start - center).normalize().let { Math.atan2(it.xd, it.yd) } + 180.toRads()
        val angle2 = (end - center).normalize().let { Math.atan2(it.xd, it.yd) } + 180.toRads()

        val change = angle2 - angle1
        val step = 16f
        val move = Math.toDegrees(change) / 360.0 * step * Config.cursorRotationSpeed

        val offset = when {
            Config.keyBindings.disableGridMotion.check(gui.input) -> Math.round(move * 16) / 16f
            Config.keyBindings.disablePixelGridMotion.check(gui.input) -> Math.round(move * 4) / 4f
            else -> Math.round(move).toFloat()
        }
        return offset * 360.0f / step
    }

    fun applyTransformation(gui: Gui, selection: ISelection, mouse: Pair<IVector2, IVector2>, canvas: Canvas): IModel? {
        val oldModel = gui.programState.model

        when (gui.state.cursor.mode) {
            CursorMode.TRANSLATION -> {
                val material = gui.state.material
                val uvDiff = getTranslationOffset(gui, mouse, canvas, material)
                if ((uvDiff - offsetCache).lengthSq() != 0.0) {
                    offsetCache = uvDiff
                    modelCache = TransformationHelper.translateTexture(oldModel, selection, uvDiff)
                }
            }

            CursorMode.ROTATION -> {
                val center = getTextureSelectionCenter(oldModel, selection, gui.state.selectedMaterial) ?: return null
                val pivot = PickupHelper.fromCanvasToMaterial(center, gui.state.material)
                val angle = getRotationOffset(gui, center, mouse, canvas)
                if ((angle - offsetCache.xf) != 0.0f) {
                    offsetCache = vec2Of(angle)
                    modelCache = TransformationHelper.rotateTexture(oldModel, selection, pivot, angle.toDouble())
                }
            }

            CursorMode.SCALE -> {
                val index = gui.state.cursor.scaleBoxIndex
                if (index == -1) return null
                val material = gui.state.material

                val min = getTextureSelectionMin(oldModel, selection, material.ref) ?: return null
                val max = getTextureSelectionMax(oldModel, selection, material.ref) ?: return null

                val start = PickupHelper.fromCanvasToMaterial(min, gui.state.material)
                val end = PickupHelper.fromCanvasToMaterial(max, gui.state.material)

                val direction = listOf(
                        vec2Of(0, -1), vec2Of(0, 1), vec2Of(-1, 0), vec2Of(1, 0),
                        vec2Of(-1, -1), vec2Of(-1, 1), vec2Of(1, -1), vec2Of(1, 1)
                )

                val uvDiff = getTranslationOffset(gui, mouse, canvas, material)
                if ((uvDiff - offsetCache).lengthSq() != 0.0) {
                    offsetCache = uvDiff
                    modelCache = TransformationHelper.scaleTexture(oldModel, selection, start, end, uvDiff, direction[index])
                }
            }
        }

        return modelCache
    }

    companion object {

        fun getBoxes(model: IModel, sel: ISelection, materialRef: IMaterialRef, camera: Camera): List<ScaleBox> {
            val min = getTextureSelectionMin(model, sel, materialRef) ?: return emptyList()
            val max = getTextureSelectionMax(model, sel, materialRef) ?: return emptyList()
            val size = max - min
            val scale = camera.zoom.toFloat() / 16f

            val edges = Cursor2DTransformHelper.getEdges(min, size, scale)
            val corners = Cursor2DTransformHelper.getCorners(min, size, scale)

            return (edges + corners).map { ScaleBox(it.first, it.second) }
        }

        fun getTextureSelectionCenter(model: IModel, selection: ISelection, materialRef: IMaterialRef): IVector2? {
            val material = model.getMaterial(materialRef)
            return when (selection.selectionType) {
                SelectionType.OBJECT -> model.getSelectedObjects(selection)
                        .map { it.mesh.tex.middle() }
                        .middle()

                SelectionType.FACE -> selection.refs
                        .filterIsInstance<IFaceRef>()
                        .map { model.getObject(it.toObjectRef()) to it.faceIndex }
                        .groupBy { it.first }
                        .map { entry -> entry.key to entry.value.map { it.second } }
                        .map { (obj, faces) ->
                            faces.map { obj.mesh.faces[it] }
                                    .flatMap { it.tex }
                                    .mapNotNull { obj.mesh.tex.getOrNull(it) }
                                    .middle()
                        }
                        .middle()

                SelectionType.EDGE -> selection.refs
                        .filterIsInstance<IEdgeRef>()
                        .map { model.getObject(it.toObjectRef()) to it }
                        .flatMap { (obj, ref) -> listOf(obj.mesh.tex[ref.firstIndex], obj.mesh.tex[ref.secondIndex]) }
                        .middle()

                SelectionType.VERTEX -> selection.refs
                        .filterIsInstance<IPosRef>()
                        .map { model.getObject(it.toObjectRef()).mesh.tex[it.posIndex] }
                        .middle()

            }.let { middle ->
                if (!middle.hasNaN()) PickupHelper.fromMaterialToCanvas(middle, material) else null
            }
        }

        fun getTextureSelectionMin(model: IModel, selection: ISelection, materialRef: IMaterialRef): IVector2? {
            val material = model.getMaterial(materialRef)
            return when (selection.selectionType) {
                SelectionType.OBJECT -> {
                    model.getSelectedObjects(selection)
                            .map { it.mesh.tex.fold(vec2Of(Double.POSITIVE_INFINITY), IVector2::min) }
                            .fold(vec2Of(Double.POSITIVE_INFINITY), IVector2::min)
                }

                SelectionType.FACE -> {
                    selection.refs
                            .filterIsInstance<IFaceRef>()
                            .map { model.getObject(it.toObjectRef()) to it.faceIndex }
                            .groupBy { it.first }
                            .map { entry -> entry.key to entry.value.map { it.second } }
                            .map { (obj, faces) ->
                                faces.map { obj.mesh.faces[it] }
                                        .flatMap { it.tex }
                                        .mapNotNull { obj.mesh.tex.getOrNull(it) }
                                        .fold(vec2Of(Double.POSITIVE_INFINITY), IVector2::min)
                            }
                            .fold(vec2Of(Double.POSITIVE_INFINITY), IVector2::min)
                }

                SelectionType.EDGE -> {
                    selection.refs
                            .filterIsInstance<IEdgeRef>()
                            .map { model.getObject(it.toObjectRef()) to it }
                            .flatMap { (obj, ref) -> listOf(obj.mesh.tex[ref.firstIndex], obj.mesh.tex[ref.secondIndex]) }
                            .fold(vec2Of(Double.POSITIVE_INFINITY), IVector2::min)
                }

                SelectionType.VERTEX -> {
                    selection.refs
                            .filterIsInstance<IPosRef>()
                            .map { model.getObject(it.toObjectRef()).mesh.tex[it.posIndex] }
                            .fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max)
                }

            }.let { size ->
                if (!size.hasNaN()) PickupHelper.fromMaterialToCanvas(size, material) else null
            }
        }

        fun getTextureSelectionMax(model: IModel, selection: ISelection, materialRef: IMaterialRef): IVector2? {
            val material = model.getMaterial(materialRef)
            return when (selection.selectionType) {
                SelectionType.OBJECT -> {
                    model.getSelectedObjects(selection)
                            .map { it.mesh.tex.fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max) }
                            .fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max)
                }

                SelectionType.FACE -> {
                    selection.refs
                            .filterIsInstance<IFaceRef>()
                            .map { model.getObject(it.toObjectRef()) to it.faceIndex }
                            .groupBy { it.first }
                            .map { entry -> entry.key to entry.value.map { it.second } }
                            .map { (obj, faces) ->
                                faces.map { obj.mesh.faces[it] }
                                        .flatMap { it.tex }
                                        .mapNotNull { obj.mesh.tex.getOrNull(it) }
                                        .fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max)
                            }
                            .fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max)
                }

                SelectionType.EDGE -> {
                    selection.refs
                            .filterIsInstance<IEdgeRef>()
                            .map { model.getObject(it.toObjectRef()) to it }
                            .flatMap { (obj, ref) -> listOf(obj.mesh.tex[ref.firstIndex], obj.mesh.tex[ref.secondIndex]) }
                            .fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max)
                }

                SelectionType.VERTEX -> {
                    selection.refs
                            .filterIsInstance<IPosRef>()
                            .map { model.getObject(it.toObjectRef()).mesh.tex[it.posIndex] }
                            .fold(vec2Of(Double.NEGATIVE_INFINITY), IVector2::max)
                }

            }.let { size ->
                if (!size.hasNaN()) PickupHelper.fromMaterialToCanvas(size, material) else null
            }
        }

        fun getEdges(pos: IVector2, size: IVector2, scale: Float): List<Pair<IVector2, IVector2>> {
            val margin = if (scale <= 1) vec2Of(scale) else vec2Of(scale.toInt())

            return listOf(
                    (pos) to vec2Of(size.xd, margin.yd),                                        // top
                    (pos + vec2Of(0, size.yd - margin.yd)) to vec2Of(size.xd, margin.yd),  // bottom
                    (pos + vec2Of(-margin.xd, 0)) to vec2Of(margin.xd, size.yd),              // left
                    (pos + vec2Of(size.xd, 0)) to vec2Of(margin.xd, size.yd)                  // right
            )
        }

        fun getCorners(pos: IVector2, size: IVector2, scale: Float): List<Pair<IVector2, IVector2>> {
            val margin = if (scale <= 1) vec2Of(scale) else vec2Of(scale.toInt())

            return listOf(
                    (pos + vec2Of(-margin.xd, 0)) to margin,                    // top-left
                    (pos + vec2Of(-margin.xd, size.yd - margin.yd)) to margin,  // bottom-left
                    (pos + vec2Of(size.xd, 0)) to margin,                       // top-right
                    (pos + vec2Of(size.xd, size.yd - margin.yd)) to margin     // bottom-right
            )
        }
    }

    data class ScaleBox(val pos: IVector2, val size: IVector2)
}