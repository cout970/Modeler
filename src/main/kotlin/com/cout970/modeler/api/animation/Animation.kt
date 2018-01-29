package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.vector.api.IVector3

interface IAnimation {
    val operations: List<IOperation>
}

interface IOperation {
    val startTime: Float
    val endTime: Float
    val objects: ISelection
}

interface ITranslationOperation : IOperation {
    val translation: IVector3
}