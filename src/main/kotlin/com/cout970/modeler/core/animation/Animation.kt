package com.cout970.modeler.core.animation

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.selection.IObjectRef

/**
 * Created by cout970 on 2017/08/20.
 */


// model to animate
data class Joint(val id: Int, val name: String, val children: List<Joint>, val offsetFromParent: ITransformation)

data class JointRef(val id: Int)
data class AnimatedModel(val model: IModel, val rootJoint: Joint, val mapObjectJoint: Map<IObjectRef, JointRef>)

// animation
data class Animation(val keyFrames: List<KeyFrame>)

data class KeyFrame(val timeStamp: Float, val transforms: Map<JointRef, ITransformation>)


class Animator(val model: AnimatedModel, val animation: Animation, val currentTime: Float)

