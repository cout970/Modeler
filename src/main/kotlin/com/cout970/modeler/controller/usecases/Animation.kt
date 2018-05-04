package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.AnimationState
import com.cout970.modeler.api.animation.InterpolationMethod
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateAnimation
import com.cout970.modeler.core.animation.Channel
import com.cout970.modeler.core.animation.Keyframe
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.project.IModelAccessor


private var lastAnimation = 0

@UseCase("animation.add")
private fun addAnimation(modelAccessor: IModelAccessor): ITask {
    val refs = modelAccessor.modelSelection.map { it.objects }.getOrNull() ?: return TaskNone
    val anim = modelAccessor.animation

    val newAnimation = anim.addChannels(listOf(
            Channel(
                    name = "Anim${lastAnimation++}",
                    interpolation = InterpolationMethod.LINEAR,
                    keyframes = listOf(
                            Keyframe(0f, TRSTransformation.IDENTITY),
                            Keyframe(anim.timeLength, TRSTransformation.IDENTITY)
                    ),
                    objects = refs
            )
    ))

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
    it.animator.animationTime = it.modelAccessor.animation.timeLength
}