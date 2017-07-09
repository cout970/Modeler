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

    val objectRefs: List<IObjectRef> get() = objects.mapIndexed { index, _ -> ObjectRef(index) }
    val materialRefs: List<IMaterialRef> get() = materials.mapIndexed { index, _ -> MaterialRef(index) }

    fun getObject(ref: IObjectRef): IObject
    fun getMaterial(ref: IMaterialRef): IMaterial

    fun withObject(obj: List<IObject>): IModel
}