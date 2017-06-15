package com.cout970.modeler.api.model.selection

import com.cout970.modeler.api.model.IModel

/**
 * Created by cout970 on 2017/05/14.
 */
interface IObjectSelection {
    val objectIndex: Int
    fun toPosSelection(model: IModel): List<IPosSelection>
}

interface IFaceSelection {
    val objectIndex: Int
    val faceIndex: Int
}

interface IPosSelection {
    val objectIndex: Int
    val posIndex: Int
}

interface ITexSelection {
    val objectIndex: Int
    val texIndex: Int
}