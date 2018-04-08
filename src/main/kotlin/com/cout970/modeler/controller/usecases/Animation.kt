package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateAnimation
import com.cout970.modeler.core.animation.Operation
import com.cout970.modeler.core.animation.TranslationDescription
import com.cout970.modeler.core.animation.animationOf
import com.cout970.modeler.core.animation.plus
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.vector.extensions.vec3Of


private var lastAnimation = 0

@UseCase("animation.add")
private fun addAnimation(modelAccessor: IModelAccessor): ITask {
    val refs = modelAccessor.modelSelection.map { it.objects }.getOrNull() ?: return TaskNone

    val newAnimation = animationOf(
            Operation(
                    description = TranslationDescription(translation = vec3Of(0, 16, 0)),
                    name = "Anim${lastAnimation++}",
                    startTime = 0.0f,
                    endTime = 1.0f,
                    objects = refs
            )
    ) + modelAccessor.animation

    return TaskUpdateAnimation(modelAccessor.animation, newAnimation)
}


@UseCase("animation.state.toggle")
private fun animationTogglePlay(): ITask = ModifyGui {
    if (it.animator.animationState == AnimationState.STOP) {
        it.animator.animationState = AnimationState.FORWARD
    } else {
        it.animator.animationState = AnimationState.STOP
    }
}

@UseCase("animation.state.backward")
private fun animationPlayBackwards(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.BACKWARD
}

@UseCase("animation.state.forward")
private fun animationPlayForward(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.FORWARD
}

@UseCase("animation.state.stop")
private fun animationStop(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.STOP
}

@UseCase("animation.seek.start")
private fun animationSeekStart(): ITask = ModifyGui {
    it.animator.animationTime = 0f
}

@UseCase("animation.seek.end")
private fun animationSeekEnd(): ITask = ModifyGui {
    it.animator.animationTime = it.animator.animationSize
}