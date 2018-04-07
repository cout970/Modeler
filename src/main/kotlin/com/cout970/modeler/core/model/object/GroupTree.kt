package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IGroupTree
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import java.util.*


// TODO fix the implementation to remove rootSet, parentMap and childMap to use another ImmutableBiMultimap
// using RootGroupRef to access rootSet
data class GroupTree(
        val rootSet: Set<IGroupRef>,
        val parentMap: Map<IGroupRef, IGroupRef>,
        val childMap: Map<IGroupRef, Set<IGroupRef>>,
        val objectMapping: ImmutableBiMultimap<IGroupRef, IObjectRef>
) : IGroupTree {

    companion object {
        fun emptyTree(): IGroupTree = GroupTree(
                emptySet(), emptyMap(), emptyMap(),
                ImmutableBiMultimapImpl.emptyBiMultimap()
        )
    }

    override fun update(validObjs: Set<IObjectRef>): GroupTree {

        val bimap = objectMapping
                .map { (group, objs) -> group to objs.filter { it in validObjs } }
                .filter { it.second.isEmpty() }
                .fold(ImmutableBiMultimapImpl.emptyBiMultimap<IGroupRef, IObjectRef>()) { map, entry ->
                    map.addAll(entry.first, entry.second)
                }

        val rootObjects = validObjs.filter { getGroup(it) == RootGroupRef }

        return copy(objectMapping = bimap.set(RootGroupRef, rootObjects.toSet()))
    }

    override fun addGroup(parent: IGroupRef, newGroupRef: IGroupRef): GroupTree {
        if (parent == RootGroupRef) {
            return if (newGroupRef !in rootSet) copy(rootSet = rootSet + newGroupRef) else this
        }

        return if (parent !in childMap) {
            if (parent != rootSet) {
                error("This group is not on the GroupTree, group = $parent, childToAdd = $newGroupRef")
            } else {
                copy(childMap = childMap + (parent to setOf(newGroupRef)))
            }
        } else {
            val children = childMap.getValue(parent)
            copy(childMap = childMap + (parent to (children + newGroupRef)))
        }
    }

    override fun removeGroup(parent: IGroupRef, child: IGroupRef): GroupTree {
        if (parent == RootGroupRef) {
            return if (child in rootSet) {
                val withoutChildGroups = removeChildren(child)
                val withoutChildObjects = getObjects(child).fold(withoutChildGroups) { tree, childChild ->
                    tree.removeObject(child, childChild)
                }
                withoutChildObjects.copy(rootSet = rootSet - child)
            } else this
        }

        return if (parent !in childMap) {
            this
        } else {
            val withoutChildGroups = removeChildren(child)
            val withoutChildObjects = getObjects(child).fold(withoutChildGroups) { tree, childChild ->
                tree.removeObject(child, childChild)
            }
            val children = withoutChildObjects.childMap.getValue(parent)
            withoutChildObjects.copy(childMap = withoutChildObjects.childMap + (parent to (children - child)))
        }
    }

    fun removeChildren(parent: IGroupRef): GroupTree {
        return getChildren(parent).fold(this) { tree, child -> tree.removeGroup(parent, child) }
    }

    override fun changeParent(child: IGroupRef, newParent: IGroupRef): GroupTree {
        return if (child !in parentMap) {
            // child's parent is in rootSet
            copy(parentMap = parentMap + (child to newParent))
        } else {
            // normal node
            copy(parentMap = (parentMap - child) + (child to newParent))
        }
    }

    override fun setObjects(parent: IGroupRef, children: List<IObjectRef>): IGroupTree {
        return copy(objectMapping = objectMapping.set(parent, children.toSet()))
    }

    override fun addObject(parent: IGroupRef, child: IObjectRef): GroupTree {
        return copy(objectMapping = objectMapping.set(parent, child))
    }

    override fun removeObject(parent: IGroupRef, child: IObjectRef): GroupTree {
        return copy(objectMapping = objectMapping.remove(parent))
    }

    override fun getParent(child: IGroupRef): IGroupRef = parentMap[child] ?: RootGroupRef

    override fun getChildren(parent: IGroupRef): List<IGroupRef> {
        if (parent == RootGroupRef) return rootSet.toList()
        return childMap[parent]?.toList() ?: emptyList()
    }

    override fun getGroup(obj: IObjectRef): IGroupRef = objectMapping.getReverse(obj) ?: RootGroupRef

    override fun getObjects(groupRef: IGroupRef): List<IObjectRef> = objectMapping[groupRef]

    override fun merge(other: IGroupTree): GroupTree {
        other as GroupTree

        var objectMapping = this.objectMapping

        other.objectMapping.forEach { (key, value) ->
            objectMapping = objectMapping.addAll(key, value)
        }

        return GroupTree(
                rootSet = this.rootSet + other.rootSet,
                parentMap = this.parentMap + other.parentMap,
                childMap = this.childMap + other.childMap,
                objectMapping = objectMapping
        )
    }
}