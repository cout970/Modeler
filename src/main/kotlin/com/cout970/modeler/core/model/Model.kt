package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.Tree
import com.cout970.modeler.api.model.TreeNode

/**
 * Created by cout970 on 2017/05/07.
 */
class Model : IModel {
    override val hierarchy: Tree<Int> = TreeNode(emptyList())
    override val objects: List<IObject> = emptyList()
}