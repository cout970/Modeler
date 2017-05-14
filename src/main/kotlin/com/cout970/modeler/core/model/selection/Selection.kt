package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.selection.IFaceSelection
import com.cout970.modeler.api.model.selection.IObjectSelection
import com.cout970.modeler.api.model.selection.IPosSelection
import com.cout970.modeler.api.model.selection.ITexSelection

/**
 * Created by cout970 on 2017/05/14.
 */
open class ObjectSelection(
        override val objectIndex: Int
) : IObjectSelection

class FaceSelection(
        objectIndex: Int,
        override val faceIndex: Int
) : ObjectSelection(objectIndex), IFaceSelection

class PosSelection(
        objectIndex: Int,
        override val posIndex: Int
) : ObjectSelection(objectIndex), IPosSelection

class TexSelection(
        objectIndex: Int,
        override val texIndex: Int
) : ObjectSelection(objectIndex), ITexSelection
