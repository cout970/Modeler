package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IGroupTree
import com.cout970.modeler.api.model.`object`.RootGroupRef
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.immutableMapOf

data class GroupTree(
        val parentMap: ImmutableMap<IGroupRef, IGroupRef>,
        val childMap: ImmutableMap<IGroupRef, Set<IGroupRef>>
) : IGroupTree {

    companion object {
        fun emptyTree(): IGroupTree = GroupTree(immutableMapOf(), immutableMapOf(RootGroupRef to emptySet()))
    }

    override fun addGroup(parent: IGroupRef, newGroupRef: IGroupRef): GroupTree {
        return if (parent !in childMap) {
            error("This group is not on the GroupTree, group = $parent, childToAdd = $newGroupRef")
        } else {
            val children = childMap.getValue(parent)
            copy(
                    childMap = childMap.put(parent, children + newGroupRef).put(newGroupRef, emptySet()),
                    parentMap = parentMap.put(newGroupRef, parent)
            )
        }
    }

    override fun removeGroup(parent: IGroupRef, child: IGroupRef): GroupTree {
        return if (parent !in childMap) {
            error("This group is not on the GroupTree, group = $parent, childToRemove = $child")
        } else {
            val children = childMap.getValue(parent)
            removeChildren(child).let { tree ->
                tree.copy(
                        childMap = tree.childMap.put(parent, (children - child)),
                        parentMap = tree.parentMap.remove(child)
                )
            }
        }
    }

    fun removeChildren(parent: IGroupRef): GroupTree {
        return getChildren(parent).fold(this) { tree, child -> tree.removeGroup(parent, child) }
    }

    override fun changeParent(child: IGroupRef, newParent: IGroupRef): GroupTree {
        return if (child !in parentMap || child !in childMap || newParent !in childMap) {
            error("This group is not on the GroupTree, child = $child, newParent = $newParent")
        } else if (child == newParent) {
            error("Error a node is trying to be a parent of itself, child: $child")
        } else {
            val oldParent = parentMap[child]!!

            val oldBrothers = childMap[oldParent]!!
            val newBrothers = childMap[newParent]!!

            copy(
                    parentMap = parentMap.put(child, newParent),
                    childMap = childMap.put(oldParent, oldBrothers - child).put(newParent, newBrothers + child)
            )
        }
    }

    override fun getParent(child: IGroupRef): IGroupRef = parentMap[child] ?: RootGroupRef

    override fun getChildren(parent: IGroupRef): List<IGroupRef> {
        return childMap[parent]?.toList() ?: emptyList()
    }

    override fun merge(other: IGroupTree): GroupTree {
        other as GroupTree

        return GroupTree(
                parentMap = this.parentMap.putAll(other.parentMap),
                childMap = this.childMap.putAll(other.childMap)
        )
    }
}