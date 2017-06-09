package com.cout970.modeler.api.model


/**
 * Created by cout970 on 2017/05/07.
 */
interface IModel {

    val objects: List<IObject>
    val hierarchy: TreeNode<Int>

    fun transformObjects(func: (List<IObject>) -> List<IObject>): IModel
}