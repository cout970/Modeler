package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IGroupTree
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.immutableHashMapOf
import kotlinx.collections.immutable.immutableMapOf
import java.util.*

// TODO fix the implementation to remove rootSet, parentMap and childMap to use another ImmutableBiMultimap
// using RootGroupRef to access rootSet
data class GroupTree(
        val parentMap: ImmutableMap<IGroupRef, IGroupRef>,
        val childMap: ImmutableMap<IGroupRef, Set<IGroupRef>>,
        val objectMapping: ImmutableBiMultimap<IGroupRef, IObjectRef>
) : IGroupTree {

    companion object {
        fun emptyTree(): IGroupTree = GroupTree(
                immutableMapOf(),
                immutableMapOf(RootGroupRef to emptySet()),
                ImmutableBiMultimapImpl.emptyBiMultimap()
        )
    }

    override fun update(validObjs: Set<IObjectRef>): GroupTree {
        val bimap = objectMapping
                .map { (group, objs) -> group to objs.filter { it in validObjs } }
                .filter { it.second.isNotEmpty() }
                .fold(ImmutableBiMultimapImpl.emptyBiMultimap<IGroupRef, IObjectRef>()) { map, entry ->
                    map.addAll(entry.first, entry.second)
                }

        val rootObjects = validObjs.filter { getGroup(it) == RootGroupRef }

        return copy(objectMapping = bimap.set(RootGroupRef, rootObjects.toSet()))
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
        if (parent !in childMap) return this

        if (child == RootGroupRef) error("Invalid child: $child")

        val withoutChildGroups = removeChildren(child)
        val withoutChildObjects = getObjects(child).fold(withoutChildGroups) { tree, childChild ->
            tree.removeObject(child, childChild)
        }
        val children = withoutChildObjects.childMap.getValue(parent)
        return withoutChildObjects.copy(childMap = withoutChildObjects.childMap.put(parent, children - child))
    }

    fun removeChildren(parent: IGroupRef): GroupTree {
        return getChildren(parent).fold(this) { tree, child -> tree.removeGroup(parent, child) }
    }

    override fun changeParent(child: IGroupRef, newParent: IGroupRef): GroupTree {
        return if (child !in parentMap || child !in childMap || newParent !in childMap) {
            error("Invalid child: $child")
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

    override fun setObjects(parent: IGroupRef, children: List<IObjectRef>): IGroupTree {
        return copy(objectMapping = objectMapping.set(parent, children.toSet()))
    }

    override fun addObject(parent: IGroupRef, child: IObjectRef): GroupTree {
        return copy(objectMapping = objectMapping.set(parent, child))
    }

    override fun removeObject(parent: IGroupRef, child: IObjectRef): GroupTree {
        return copy(objectMapping = objectMapping.removeValue(parent, child))
    }

    override fun getParent(child: IGroupRef): IGroupRef = parentMap[child] ?: RootGroupRef

    override fun getChildren(parent: IGroupRef): List<IGroupRef> {
        return childMap[parent]?.toList() ?: error("Invalid group: $parent")
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
                parentMap = this.parentMap.putAll(other.parentMap),
                childMap = this.childMap.putAll(other.childMap),
                objectMapping = objectMapping
        )
    }
}