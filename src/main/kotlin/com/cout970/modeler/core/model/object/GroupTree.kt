package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.`object`.IGroup
import com.cout970.modeler.api.model.`object`.IGroupTree
import com.cout970.modeler.api.model.selection.IObjectRef

data class GroupTree(
        val rootSet: Set<IGroup>,
        val parentMap: Map<IGroup, IGroup>,
        val childMap: Map<IGroup, Set<IGroup>>,
        val objectMapping: ImmutableBiMultimap<IGroup, IObjectRef>
) : IGroupTree {

    companion object {
        fun emptyTree(): IGroupTree = GroupTree(
                emptySet(), emptyMap(), emptyMap(),
                ImmutableBiMultimapImpl.emptyBimap()
        )
    }

    override val root: List<IGroup> get() = rootSet.toList()

    override fun addGroup(parent: IGroup?, newGroup: IGroup): IGroupTree {
        if (parent == null) {
            return if (newGroup !in root) copy(rootSet = rootSet + newGroup) else this
        }

        return if (parent !in childMap) {
            if (parent != rootSet) {
                error("This group is not on the GroupTree, group = $parent, childToAdd = $newGroup")
            } else {
                copy(childMap = childMap + (parent to setOf(newGroup)))
            }
        } else {
            val children = childMap.getValue(parent)
            copy(childMap = childMap + (parent to (children + newGroup)))
        }
    }

    override fun removeGroup(parent: IGroup?, child: IGroup): IGroupTree {
        if (parent == null) {
            return if (child in root) copy(rootSet = rootSet - child) else this
        }

        return if (parent !in childMap) {
            this
        } else {
            val children = childMap.getValue(parent)
            copy(childMap = childMap + (parent to (children - child)))
        }
    }

    override fun changeParent(child: IGroup, newParent: IGroup): IGroupTree {
        return if (child !in parentMap) {
            // child's parent is in rootSet
            copy(parentMap = parentMap + (child to newParent))
        } else {
            // normal node
            copy(parentMap = (parentMap - child) + (child to newParent))
        }
    }

    override fun addObject(parent: IGroup, child: IObjectRef): IGroupTree {
        return copy(objectMapping = objectMapping.set(parent, child))
    }

    override fun removeObject(parent: IGroup, child: IObjectRef): IGroupTree {
        return copy(objectMapping = objectMapping.remove(parent))
    }

    override fun getParent(child: IGroup): IGroup? = parentMap[child]

    override fun getChildren(parent: IGroup): List<IGroup> = childMap[parent]?.toList() ?: emptyList()

    override fun getGroup(obj: IObjectRef): IGroup? = objectMapping.getReverse(obj)

    override fun getObjects(group: IGroup): List<IObjectRef> = objectMapping[group]
}