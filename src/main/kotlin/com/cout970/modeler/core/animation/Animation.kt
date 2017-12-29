package com.cout970.modeler.core.animation

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IJoint
import com.cout970.modeler.api.animation.IJointRef
import com.cout970.modeler.api.animation.IKeyFrame
import com.cout970.modeler.api.model.ITransformation

/**
 * Created by cout970 on 2017/08/20.
 */


data class Joint(
        override val id: Int,
        override val name: String,
        override val children: List<IJoint>,
        override val offsetFromParent: ITransformation
) : IJoint

data class JointRef(override val id: Int) : IJointRef

data class Animation(
        override val keyFrames: List<IKeyFrame>,
        override val rootJoint: IJoint
) : IAnimation

data class KeyFrame(
        override val timeStamp: Float,
        override val transforms: Map<IJointRef, ITransformation>
) : IKeyFrame
