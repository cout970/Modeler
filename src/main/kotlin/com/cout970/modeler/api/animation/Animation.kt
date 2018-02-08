package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import java.util.*

interface IAnimation {
    val operations: Map<IOperationRef, IOperation>

    fun addOperations(list: List<IOperation>): IAnimation
    fun removeOperations(list: List<IOperationRef>): IAnimation

    fun getOperations(obj: IObjectRef): List<IOperation>
}

interface IOperationRef {
    val operationId: UUID
}

interface IOperation {
    val id: UUID
    val name: String
    val startTime: Float
    val endTime: Float
    val objects: List<IObjectRef>
    val description: IOperationDescription
}

interface IOperationDescription

interface ITranslationDescription : IOperationDescription {
    val translation: IVector3
}

interface IRotationDescription : IOperationDescription {
    val rotation: IQuaternion
}

interface IScaleDescription : IOperationDescription {
    val scale: IVector3
}

enum class AnimationState {
    STOP, FORWARD, BACKWARD
}