package com.cout970.modeler.api.model

import com.cout970.modeler.api.model.`object`.IGroup
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IGroupTree
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef


/**
 * Created by cout970 on 2017/05/07.
 */
interface IModel : Comparable<IModel> {
    val objectMap: Map<IObjectRef, IObject>
    val materialMap: Map<IMaterialRef, IMaterial>
    val groupMap: Map<IGroupRef, IGroup>

    val groupTree: IGroupTree

    val materialRefs: List<IMaterialRef> get() = materialMap.keys.toList()
    val materials: List<IMaterial> get() = materialMap.values.toList()

    val objectRefs: List<IObjectRef> get() = objectMap.keys.toList()
    val objects: List<IObject> get() = objectMap.values.toList()

    fun getObject(ref: IObjectRef): IObject
    fun getMaterial(ref: IMaterialRef): IMaterial
    fun getGroup(ref: IGroupRef): IGroup

    fun addObjects(objs: List<IObject>): IModel
    fun removeObjects(objs: List<IObjectRef>): IModel

    fun modifyObjects(predicate: (IObjectRef) -> Boolean, func: (IObjectRef, IObject) -> IObject): IModel
    fun addMaterial(material: IMaterial): IModel
    fun modifyMaterial(ref: IMaterialRef, new: IMaterial): IModel

    fun removeMaterial(materialRef: IMaterialRef): IModel
    fun addGroup(group: IGroup): IModel
    fun modifyGroup(ref: IGroupRef, group: IGroup): IModel

    fun removeGroup(ref: IGroupRef): IModel

    fun withGroupTree(newGroupTree: IGroupTree): IModel

    fun modifyObjects(objs: Set<IObjectRef>, func: (IObjectRef, IObject) -> IObject): IModel {
        return modifyObjects({ it in objs }, func)
    }

    fun merge(other: IModel): IModel
}