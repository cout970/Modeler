package com.cout970.modeler.core.animation

import com.cout970.modeler.api.model.ITransformation

/**
 * Created by cout970 on 2017/08/20.
 */


data class Joint(val id: Int, val name: String, val children: List<Joint>, val offsetFromParent: ITransformation)

data class JointRef(val id: Int)

data class Animation(val keyFrames: List<KeyFrame>, val rootJoint: Joint)

data class KeyFrame(val timeStamp: Float, val transforms: Map<JointRef, ITransformation>)

class Animator(val animation: Animation, var currentTime: Float)

