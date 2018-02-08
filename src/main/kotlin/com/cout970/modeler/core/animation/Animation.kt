package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import java.util.*

/**
 * Created by cout970 on 2017/08/20.
 */

data class Animation(
        override val operations: Map<IOperationRef, IOperation>
) : IAnimation {

    override fun addOperations(list: List<IOperation>): IAnimation {
        return Animation(operations + list.associateBy { OperationRef(it.id) })
    }

    override fun removeOperations(list: List<IOperationRef>): IAnimation {
        return Animation(operations.filterKeys { it !in list })
    }

    override fun getOperations(obj: IObjectRef): List<IOperation> {
        return operations.values.filter { obj in it.objects }
    }
}

data class OperationRef(override val operationId: UUID) : IOperationRef

data class Operation(
        override val description: IOperationDescription,
        override val name: String,
        override val startTime: Float,
        override val endTime: Float,
        override val objects: List<IObjectRef>,
        override val id: UUID = UUID.randomUUID()
) : IOperation

data class TranslationDescription(override val translation: IVector3) : ITranslationDescription
data class RotationDescription(override val rotation: IQuaternion) : IRotationDescription
data class ScaleDescription(override val scale: IVector3) : IScaleDescription

inline val IOperation.ref get() = OperationRef(id)

inline fun animationOf(vararg operations: IOperation) = Animation(operations.associateBy { it.ref })

operator fun IAnimation.plus(other: IAnimation) = addOperations(other.operations.values.toList())