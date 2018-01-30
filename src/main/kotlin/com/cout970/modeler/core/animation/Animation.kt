package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IOperation
import com.cout970.modeler.api.animation.IOperationRef
import com.cout970.modeler.api.animation.ITranslationOperation
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.vector.api.IVector3
import java.util.*

/**
 * Created by cout970 on 2017/08/20.
 */

data class Animation(
        override val operations: Map<IOperationRef, IOperation>
) : IAnimation

data class OperationRef(override val operationId: UUID) : IOperationRef

data class TranslationOperation(
        override val startTime: Float,
        override val endTime: Float,
        override val objects: List<IObjectRef>,
        override val translation: IVector3,
        override val id: UUID = UUID.randomUUID()
) : ITranslationOperation

inline val IOperation.ref get() = OperationRef(id)

inline fun animationOf(vararg operations: IOperation) = Animation(operations.associateBy { it.ref })

operator fun IAnimation.plus(other: IAnimation) = Animation(this.operations + other.operations)