package com.cout970.modeler.core.model

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IAnimationRef
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.model.`object`.ObjectNone
import com.cout970.modeler.core.model.`object`.emptyBiMultimap
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone

/**
 * Created by cout970 on 2017/05/07.
 *
 * Immutable snapshot of the model in a point of time, Is immutable to reduce memory usage sharing objects and
 * materials with the new versions, also makes concurrency and transactions simpler
 */
data class Model(
        override val objectMap: Map<IObjectRef, IObject>,
        override val materialMap: Map<IMaterialRef, IMaterial>,
        override val groupMap: Map<IGroupRef, IGroup>,
        override val animationMap: Map<IAnimationRef, IAnimation>,
        override val tree: ImmutableGroupTree
) : IModel {

    val id: Int = lastId++

    companion object {
        private var lastId = 0

        fun of(objects: List<IObject> = emptyList(),
               materials: List<IMaterial> = emptyList(),
               groups: List<IGroup> = emptyList()
        ): IModel {
            return of(objects.associateBy { it.ref }, materials.associateBy { it.ref }, groups.associateBy { it.ref })
        }

        fun of(objectMap: Map<IObjectRef, IObject> = emptyMap(),
               materialMap: Map<IMaterialRef, IMaterial> = emptyMap(),
               groupMap: Map<IGroupRef, IGroup> = emptyMap(),
               animationMap: Map<IAnimationRef, IAnimation> = emptyMap(),
               groupTree: ImmutableGroupTree = ImmutableGroupTree(emptyBiMultimap(), emptyBiMultimap())
        ): IModel {
            return Model(objectMap, materialMap, groupMap, animationMap, groupTree)
        }

        fun empty() = Model()
    }

    private constructor() : this(emptyMap(), emptyMap(), emptyMap(), emptyMap(), ImmutableGroupTree(emptyBiMultimap(), emptyBiMultimap()))

    fun updateTree(): IModel {
        val unGroupedObjects = objectMap.keys.filter { tree.objects.getReverse(it) == null }
        val phantomObjects = tree.objects.flatMap { it.second }.filter { it !in objectMap }

        if (unGroupedObjects.isNotEmpty() || phantomObjects.isNotEmpty()) {
            val newTree = tree.mutate {
                objects += unGroupedObjects
                objects.removeAll { it !in objectMap }
            }
            return this.withGroupTree(newTree)
        }

        return this
    }

    override fun getObject(ref: IObjectRef): IObject {
        if (ref in objectMap) {
            return objectMap[ref]!!
        }
        return ObjectNone
    }

    override fun getMaterial(ref: IMaterialRef): IMaterial {
        if (ref in materialMap) {
            return materialMap[ref]!!
        }
        return MaterialNone
    }

    override fun getGroup(ref: IGroupRef): IGroup {
        if (ref in groupMap) {
            return groupMap[ref]!!
        }
        return GroupNone
    }

    override fun addObjects(objs: List<IObject>): IModel {
        val newObjs = objectMap + objs.map { it.toPair() }
        return copy(
                objectMap = newObjs,
                tree = tree.mutate { objects.addAll(objs.map { it.ref }) }
        ).updateTree()
    }

    override fun removeObjects(objs: List<IObjectRef>): IModel {
        val toRemove = objs.toSet()
        val newObjs = objectMap.filter { (index, _) -> index !in toRemove }
        return copy(
                objectMap = newObjs,
                tree = tree.mutate { removeObjects(toRemove) }
        ).updateTree()
    }

    override fun modifyObjects(predicate: (IObjectRef) -> Boolean, func: (IObjectRef, IObject) -> IObject): IModel {
        val newObjs = objectMap.map { (ref, iObject) ->
            if (predicate(ref)) {
                val newObj = func(ref, iObject)
                newObj.ref to newObj
            } else {
                iObject.ref to iObject
            }
        }.toMap()

        return copy(objectMap = newObjs).updateTree()
    }

    override fun addMaterial(material: IMaterial): IModel {
        return copy(materialMap = materialMap + (material.ref to material))
    }

    override fun modifyMaterial(ref: IMaterialRef, new: IMaterial): IModel {
        return copy(
                objectMap = objectMap.mapValues { (_, obj) ->
                    if (obj.material == ref) obj.withMaterial(new.ref) else obj
                },
                materialMap = materialMap.toMutableMap().apply { remove(ref); put(new.ref, new) }
        )
    }

    override fun removeMaterial(materialRef: IMaterialRef): IModel {
        return copy(
                materialMap = materialMap.toMutableMap().apply { remove(materialRef) },
                objectMap = objectMap.map { (_, it) ->
                    val newObj = when (materialRef) {
                        it.material -> it.withMaterial(MaterialRefNone)
                        else -> it
                    }
                    newObj.ref to newObj
                }.toMap()
        )
    }

    override fun addGroup(group: IGroup): IModel {
        require(group.ref !in groupMap)
        return copy(groupMap = groupMap + (group.ref to group)).updateTree()
    }

    override fun modifyGroup(ref: IGroupRef, group: IGroup): IModel {
        require(ref == group.ref)
        return copy(groupMap = groupMap + (group.ref to group)).updateTree()
    }

    override fun removeGroup(ref: IGroupRef): IModel {
        val groups = getRecursiveChildGroups(ref) + ref
        val objs = getRecursiveChildObjects(ref)
        val newObjs = objectMap - objs

        return copy(
                objectMap = newObjs,
                groupMap = groupMap - groups,
                tree = tree.mutate { removeGroup(ref) }
        ).updateTree()
    }

    override fun addAnimation(animation: IAnimation): IModel {
        require(animation.ref !in animationMap)
        return copy(animationMap = animationMap + (animation.ref to animation))
    }

    override fun modifyAnimation(ref: IAnimationRef, new: IAnimation): IModel {
        require(ref == new.ref)
        return copy(animationMap = animationMap + (new.ref to new))
    }

    override fun removeAnimation(animationRef: IAnimationRef): IModel {
        return copy(animationMap = animationMap - animationRef)
    }

    override fun withGroupTree(newGroupTree: ImmutableGroupTree): IModel {
        return copy(tree = newGroupTree)
    }

    override fun merge(other: IModel): IModel {
        return Model(
                objectMap = this.objectMap + other.objectMap,
                materialMap = this.materialMap + other.materialMap,
                groupMap = this.groupMap + other.groupMap,
                animationMap = this.animationMap + other.animationMap,
                tree = tree.mutate {
                    val otherTree = other.tree.toMutable()
                    objects += otherTree.objects
                    children += otherTree.children
                }
        )
    }

    override fun compareTo(other: IModel): Int {
        if (this.objectMap != other.objectMap) return -1
        if (this.materialMap != other.materialMap) return -1
        if (this.groupMap != other.groupMap) return -1
        if (this.animationMap != other.animationMap) return -1
        if (this.tree != other.tree) return -1

        return 0
    }

    override fun equals(other: Any?): Boolean = id == (other as? Model)?.id

    override fun hashCode(): Int = id
}