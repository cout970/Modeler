package com.cout970.modeler.api.model


/**
 * Created by cout970 on 2017/05/07.
 */
sealed class Tree<T>

class TreeNode<T>(val children: List<Tree<T>>) : Tree<T>()
class TreeLeaf<T>(val value: T) : Tree<T>()