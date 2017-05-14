package com.cout970.modeler.api.model.selection

/**
 * Created by cout970 on 2017/05/14.
 */
interface IObjectSelection {
    val objectIndex: Int
}

interface IFaceSelection : IObjectSelection {
    val faceIndex: Int
}

interface IPosSelection : IObjectSelection {
    val posIndex: Int
}

interface ITexSelection : IObjectSelection {
    val texIndex: Int
}