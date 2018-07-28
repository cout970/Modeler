package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.helpers.TransformationHelper
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.gui.canvas.helpers.RotationHelper
import com.cout970.modeler.gui.canvas.helpers.ScaleHelper
import com.cout970.modeler.gui.canvas.helpers.TranslationHelper
import com.cout970.modeler.util.toAxisRotations
import com.cout970.modeler.util.toIQuaternion
import com.cout970.modeler.util.toRads
import com.cout970.modeler.util.transform
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.unaryMinus
import org.joml.Quaterniond

class Cursor3DTransformHelper {

    var cache: IModel? = null
    var offset: Float = 0f

    fun applyTransformation(gui: Gui, selection: ISelection, cursor: Cursor3D, hovered: CursorPart,
                            mouse: Pair<IVector2, IVector2>, canvas: Canvas): IModel {

        val oldModel = gui.programState.model
        val modelCache = this.cache ?: oldModel
        val vector = cursor.rotation.transform(hovered.vector)

        val newOffset = when (hovered.mode) {
            CursorMode.TRANSLATION -> {
                val context = CanvasHelper.getContext(canvas, mouse)
                TranslationHelper.getOffset(-vector, canvas, gui.input, context.first, context.second)
            }
            CursorMode.ROTATION -> {
                RotationHelper.getOffsetGlobal(cursor.position, vector, canvas, mouse, gui.input)
            }
            CursorMode.SCALE -> {
                val context = CanvasHelper.getContext(canvas, mouse)
                ScaleHelper.getOffset(-vector, canvas, gui.input, context.first, context.second)
            }
        }

        if (newOffset != offset) {
            this.offset = newOffset

            val model = when (hovered.mode) {
                CursorMode.TRANSLATION -> {
                    val transform = TRSTransformation(translation = vector * offset)
                    TransformationHelper.transformLocal(oldModel, selection, gui.animator, transform)
                }
                CursorMode.ROTATION -> {
                    val transform = TRSTransformation.fromRotationPivot(cursor.position, quatOfAxisAngled(vector, offset).toAxisRotations())
                    TransformationHelper.transformLocal(oldModel, selection, gui.animator, transform)
                }
                CursorMode.SCALE -> {
                    if (cursor.useLinearScalation) {
                        TransformationHelper.scaleLocal(oldModel, selection, gui.animator, vector, offset)
                    } else {
                        val transform = TRSTransformation.fromScalePivot(cursor.position, Vector3.ONE + vector * offset)
                        TransformationHelper.transformLocal(oldModel, selection, gui.animator, transform)
                    }
                }
            }

            this.cache = model
            return model
        } else {
            this.cache = modelCache
            this.offset = newOffset
            return this.cache!!
        }
    }

    // degrees
    fun quatOfAxisAngled(angles: IVector3, angle: Number): IQuaternion {

        return Quaterniond().rotateAxis(
                angle.toDouble(),
                angles.x.toRads(),
                angles.y.toRads(),
                angles.z.toRads()
        ).toIQuaternion()
    }
}
