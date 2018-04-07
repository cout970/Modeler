package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.helpers.DeletionHelper
import com.cout970.modeler.core.model.`object`.GroupTree
import com.cout970.modeler.core.model.`object`.ObjectNone
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone

/**
 * Created by cout970 on 2017/05/07.
 *
 * Immutable snapshot of the model in a point of time, Is immutable to reduce memory usage sharing objects and
 * materials with the new versions
 */
data class Model(
        override val objectMap: Map<IObjectRef, IObject>,
        override val materialMap: Map<IMaterialRef, IMaterial>,
        override val groupMap: Map<IGroupRef, IGroup>,
        override val groupTree: IGroupTree
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
               groupTree: IGroupTree = GroupTree.emptyTree()
        ): IModel {
            return Model(objectMap, materialMap, groupMap, groupTree)
        }

        fun empty() = Model()
    }

    private constructor() : this(emptyMap(), emptyMap(), emptyMap(), GroupTree.emptyTree())

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
        return copy(
                objectMap = objectMap + objs.map { it.toPair() },
                materialMap = materialMap
        )
    }

    override fun removeObjects(objs: List<IObjectRef>): IModel {
        val toRemove = objs.toSet()
        return copy(
                objectMap = objectMap.filter { (index, _) -> index !in toRemove },
                materialMap = materialMap
        )
    }

    override fun modifyObjects(predicate: (IObjectRef) -> Boolean, func: (IObjectRef, IObject) -> IObject): IModel {
        return copy(
                objectMap = objectMap.map { (ref, iObject) ->
                    if (predicate(ref)) {
                        val newObj = func(ref, iObject)
                        newObj.ref to newObj
                    } else {
                        iObject.ref to iObject
                    }
                }.toMap(),
                materialMap = materialMap
        )
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
        return copy(groupMap = groupMap + (group.ref to group))
    }

    override fun modifyGroup(ref: IGroupRef, group: IGroup): IModel {
        require(ref == group.ref)
        return copy(groupMap = groupMap + (group.ref to group))
    }

    override fun removeGroup(ref: IGroupRef): IModel {
        val groups = getRecursiveChildGroups(ref) + ref
        val objs = getRecursiveChildObjects(ref) + groupTree.getObjects(ref)
        val tree = groupTree.removeGroup(groupTree.getParent(ref), ref)

        return copy(
                objectMap = objectMap - objs,
                groupMap = groupMap - groups,
                groupTree = tree
        )
    }

    override fun withGroupTree(newGroupTree: IGroupTree): IModel {
        return copy(groupTree = newGroupTree)
    }

    override fun merge(other: IModel): IModel {
        return Model(
                objectMap = this.objectMap + other.objectMap,
                materialMap = this.materialMap + other.materialMap,
                groupMap = this.groupMap + other.groupMap,
                groupTree = this.groupTree.merge(other.groupTree)
        )
    }

    override fun compareTo(other: IModel): Int {
        if (this.objectMap != other.objectMap) return -1
        if (this.materialMap != other.materialMap) return -1
        if (this.groupMap != other.groupMap) return -1

        return 0
    }

    override fun equals(other: Any?): Boolean = id == (other as? Model)?.id

    override fun hashCode(): Int = id
}