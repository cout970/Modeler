package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IOperation
import com.cout970.modeler.api.animation.ITranslationOperation
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/08/20.
 */

data class Animation(
        override val operations: List<IOperation>
) : IAnimation

data class Operation(
        override val startTime: Float,
        override val endTime: Float,
        override val objects: ISelection
) : IOperation

data class TranslationOperation(
        override val startTime: Float,
        override val endTime: Float,
        override val objects: ISelection,
        override val translation: IVector3
) : ITranslationOperation