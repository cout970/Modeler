package com.cout970.modeler.api.animation

interface IAnimation {
    val keyFrames: List<IKeyFrame>
    val rootJoint: IJoint
}