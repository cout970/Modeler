package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.material.MaterialNone

/**
 * Created by cout970 on 2017/05/07.
 */

data class Model(
        override val objects: List<IObject> = emptyList(),
        override val materials: List<IMaterial> = emptyList()
) : IModel {

    val id: Int = lastId++

    override fun getObject(ref: IObjectRef): IObject {
        if (ref.objectIndex in objects.indices) {
            return objects[ref.objectIndex]
        }
        return ObjectNone
    }

    override fun getMaterial(ref: IMaterialRef): IMaterial {
        if (ref.materialIndex in materials.indices) {
            return materials[ref.materialIndex]
        }
        return MaterialNone
    }

    override fun withObject(obj: List<IObject>): IModel {
        return copy(objects = obj)
    }

    companion object {
        private var lastId = 0
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? Model)?.id
    }

    override fun hashCode(): Int {
        return id
    }
}