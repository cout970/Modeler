package com.cout970.modeler.core.helpers

import com.cout970.modeler.api.animation.AnimationTargetObject
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IChannelRef
import com.cout970.modeler.api.animation.IKeyframe
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.core.animation.AnimationNone
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.getParentGlobalTransform
import com.cout970.modeler.core.model.toTRS
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.invert
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toJOML
import com.cout970.modeler.util.transform
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.max
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

object AnimationHelper {

    fun editChannel(model: IModel, animator: Animator, func: (IKeyframe) -> IKeyframe): IModel? {
        val animation = animator.animation
        if (animation == AnimationNone) return null
        val channelRef = animator.selectedChannel ?: return null
        val channel = animation.channels[channelRef] ?: return null

        val newKeyframes = channel.keyframes.map(func)
        val newChannel = channel.withKeyframes(newKeyframes)
        val newAnimation = animation.withChannel(newChannel)

        return model.modifyAnimation(newAnimation)
    }

    fun transformKeyframe(diff: ITransformation, model: IModel, animator: Animator): IModel {
        return model.modifyAnimation(transformAnimationKeyframe(diff, animator))
    }

    fun transformAnimationKeyframe(diff: ITransformation, animator: Animator): IAnimation {
        return editKeyframe(animator.animation, animator.selectedChannel!!, animator.selectedKeyframe!!) { keyframe ->
            keyframe.withValue(keyframe.value + diff)
        }
    }

    fun editKeyframe(animation: IAnimation, channelRef: IChannelRef, keyframeIndex: Int, func: (IKeyframe) -> IKeyframe): IAnimation {
        val channel = animation.channels[channelRef]!!
        val keyframe = channel.keyframes[keyframeIndex]

        val newKeyframe = func(keyframe)
        val prev = channel.keyframes.filter { it.time < keyframe.time }
        val next = channel.keyframes.filter { it.time > keyframe.time }

        val newChannel = channel.withKeyframes(prev + newKeyframe + next)
        return animation.withChannel(newChannel)
    }

    fun scaleKeyframe(oldModel: IModel, animator: Animator, vector: IVector3, offset: Float): IModel {
        val target = animator.animation.channelMapping[animator.selectedChannel!!]
                as? AnimationTargetObject ?: return oldModel

        // TODO
        val obj = oldModel.getObject(target.refs.first())

        val mat = obj.getParentGlobalTransform(oldModel, animator)
        val invMatrix = mat.matrix.toJOML().invert().toIMatrix()
        val inv = TRSTransformation.fromMatrix(invMatrix)

        val trs = obj.transformation.toTRS()

        val dir = (inv.invert() + TRSTransformation(vector) + inv).toTRS().translation

        val local = trs.rotation.invert().transform(dir)
        val (scale, translation) = TransformationHelper.getScaleAndTranslation(local)
        val finalTranslation = trs.rotation.transform(translation)

        val keyframeTransform = trs.copy(
                translation = trs.translation + finalTranslation * offset,
                scale = (trs.scale + scale * offset).max(Vector3.ZERO)
        )

        val anim = editKeyframe(animator.animation, animator.selectedChannel!!, animator.selectedKeyframe!!) { keyframe ->
            keyframe.withValue(keyframeTransform)
        }

        return oldModel.modifyAnimation(anim)
    }
}