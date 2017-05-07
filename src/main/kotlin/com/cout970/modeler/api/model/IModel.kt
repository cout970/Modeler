package com.cout970.modeler.api.model


/**
 * Created by cout970 on 2017/05/07.
 */
interface IModel {

    val hierarchy: Tree<Int>
    val objects: List<IObject>
}