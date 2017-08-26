package com.cout970.modeler.gui.canvas.input

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.*
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.gui.canvas.helpers.RotationHelper
import com.cout970.modeler.gui.canvas.helpers.ScaleHelper
import com.cout970.modeler.gui.canvas.helpers.TranslationHelper
import com.cout970.modeler.input.event.IInput
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/08/16.
 */

data class TransformationStep(
        val model: IModel? = null,
        val offset: Float = 0f
)

fun TransformationStep.next(gui: Gui, hovered: ISelectable, pos: Pair<IVector2, IVector2>,
                            canvas: Canvas): TransformationStep {

    val mode = gui.state.transformationMode
    val oldModel = gui.projectManager.model

    val newOffset = when {
        hovered is ITranslatable && mode == TransformationMode.TRANSLATION -> {
            getTranslationOffset(hovered, canvas, pos, gui.input)
        }
        hovered is IRotable && mode == TransformationMode.ROTATION -> {
            getRotationOffset(hovered, canvas, pos, gui.input)
        }
        hovered is IScalable && mode == TransformationMode.SCALE -> {
            getScaleOffset(hovered, canvas, pos, gui.input)
        }
        else -> 0f
    }

    if (newOffset != offset) {

        gui.selectionHandler.getModelSelection().map { sel ->
            val part = oldModel to sel

            val model = when {
                hovered is ITranslatable && mode == TransformationMode.TRANSLATION -> {
                    applyTranslationOffset(hovered, part, newOffset)
                }
                hovered is IRotable && mode == TransformationMode.ROTATION -> {
                    applyRotationOffset(hovered, part, newOffset)
                }
                hovered is IScalable && mode == TransformationMode.SCALE -> {
                    applyScaleOffset(hovered, part, newOffset)
                }
                else -> null
            }

            return TransformationStep(model, newOffset)
        }
    }
    return this
}


private fun getScaleOffset(obj: IScalable, canvas: Canvas, pos: Pair<IVector2, IVector2>, input: IInput): Float {
    val context = CanvasHelper.getContext(canvas, pos)

    return ScaleHelper.getOffset(
            obj = obj,
            canvas = canvas,
            input = input,
            newContext = context.first,
            oldContext = context.second
    )
}

private fun applyScaleOffset(obj: IScalable, part: Pair<IModel, ISelection>, offset: Float): IModel =
        obj.applyScale(offset, part.second, part.first)

private fun getRotationOffset(obj: IRotable, canvas: Canvas, pos: Pair<IVector2, IVector2>, input: IInput): Float {
    val context = CanvasHelper.getContext(canvas, pos)

    return RotationHelper.getOffset(
            obj = obj,
            input = input,
            newContext = context.first,
            oldContext = context.second
    )
}

private fun applyRotationOffset(obj: IRotable, part: Pair<IModel, ISelection>, offset: Float): IModel =
        obj.applyRotation(offset, part.second, part.first)


private fun getTranslationOffset(obj: ITranslatable, canvas: Canvas, pos: Pair<IVector2, IVector2>,
                                 input: IInput): Float {

    val context = CanvasHelper.getContext(canvas, pos)

    return TranslationHelper.getOffset(
            obj = obj,
            canvas = canvas,
            input = input,
            newContext = context.first,
            oldContext = context.second
    )
}

private fun applyTranslationOffset(obj: ITranslatable, part: Pair<IModel, ISelection>, offset: Float): IModel =
        obj.applyTranslation(offset, part.second, part.first)

