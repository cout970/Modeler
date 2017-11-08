package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.ITransformation

interface IKeyFrame {
    val timeStamp: Float
    val transforms: Map<IJointRef, ITransformation>
}