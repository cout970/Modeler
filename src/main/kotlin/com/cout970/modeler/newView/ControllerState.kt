package com.cout970.modeler.newView

import com.cout970.modeler.util.BooleanProperty

/**
 * Created by cout970 on 2017/04/08.
 */
class ControllerState(
        var transformationMode: TransformationMode = TransformationMode.TRANSLATION,
        val showAllMeshUVs: BooleanProperty = BooleanProperty(true),
        val showBoundingBoxes: BooleanProperty = BooleanProperty(false)
)