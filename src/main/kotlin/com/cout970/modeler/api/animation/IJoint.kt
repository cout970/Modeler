package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.ITransformation

interface IJoint {
    val id: Int
    val name: String
    val children: List<IJoint>
    val offsetFromParent: ITransformation
}