package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.TreeNode

/**
 * Created by cout970 on 2017/05/07.
 */

data class Model(
        override val objects: List<IObject> = emptyList(),
        override val hierarchy: TreeNode<Int> = TreeNode(emptyList())
) : IModel {

    val id: Int = lastId++

    override fun transformObjects(func: (List<IObject>) -> List<IObject>): IModel {
        return copy(objects = func(objects))
    }

    companion object {
        private var lastId = 0
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? Model)?.id
    }

    override fun hashCode(): Int {
        return id
    }
}