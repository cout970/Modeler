package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.vector.api.IVector3
import java.util.*

interface IAnimation {
    val operations: Map<IOperationRef, IOperation>
}

interface IOperationRef {
    val operationId: UUID
}

interface IOperation {
    val id: UUID
    val startTime: Float
    val endTime: Float
    val objects: List<IObjectRef>
}

interface ITranslationOperation : IOperation {
    val translation: IVector3
}