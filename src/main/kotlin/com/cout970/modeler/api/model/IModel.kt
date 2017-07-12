package com.cout970.modeler.api.model

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.selection.ObjectRef


/**
 * Created by cout970 on 2017/05/07.
 */
interface IModel {
    val objects: List<IObject>
    val materials: List<IMaterial>
    val visibilities: List<Boolean>

    val objectRefs: List<IObjectRef> get() = objects.mapIndexed { index, _ -> ObjectRef(index) }
    val materialRefs: List<IMaterialRef> get() = materials.mapIndexed { index, _ -> MaterialRef(index) }

    fun getObject(ref: IObjectRef): IObject
    fun getMaterial(ref: IMaterialRef): IMaterial
    fun isVisible(ref: IObjectRef): Boolean

    fun setVisible(ref: IObjectRef, visible: Boolean): IModel

    fun addObjects(objs: List<IObject>): IModel
    fun removeObjects(objs: List<IObjectRef>): IModel

    fun modifyObjects(predicate: (IObjectRef) -> Boolean, func: (IObjectRef, IObject) -> IObject): IModel

    fun modifyObjects(objs: List<IObjectRef>, func: (IObjectRef, IObject) -> IObject): IModel {
        return modifyObjects({ it in objs }, func)
    }

    fun addMaterial(material: IMaterial): IModel
    fun modifyMaterial(ref: IMaterialRef, new: IMaterial): IModel
    fun removeMaterial(materialRef: IMaterialRef): IModel
}