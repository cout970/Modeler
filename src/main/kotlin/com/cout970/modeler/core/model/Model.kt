package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.selection.ObjectRef

/**
 * Created by cout970 on 2017/05/07.
 */

data class Model(
        override val objects: List<IObject>,
        override val materials: List<IMaterial>,
        override val visibilities: List<Boolean>
) : IModel {

    val id: Int = lastId++

    companion object {
        private var lastId = 0

        fun of(objects: List<IObject>, materials: List<IMaterial>): IModel {
            return Model(objects, materials, objects.map { true })
        }

        fun empty() = Model(emptyList(), emptyList(), emptyList())
    }

    override fun isVisible(ref: IObjectRef): Boolean {
        if (ref.objectIndex in objects.indices) {
            return visibilities[ref.objectIndex]
        }
        return false
    }

    override fun setVisible(ref: IObjectRef, visible: Boolean): IModel {
        return copy(
                visibilities = visibilities.mapIndexed { index, value ->
                    if (index == ref.objectIndex) visible else value
                }
        )
    }

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

    override fun addObjects(objs: List<IObject>): IModel {
        return copy(
                objects = objects + objs,
                materials = materials,
                visibilities = visibilities + objs.map { true }
        )
    }

    override fun removeObjects(objs: List<IObjectRef>): IModel {
        val toRemove = objs.map { it.objectIndex }
        return copy(
                objects = objects.filterIndexed { index, _ -> index !in toRemove },
                materials = materials,
                visibilities = visibilities.filterIndexed { index, _ -> index !in toRemove }
        )
    }

    override fun modifyObjects(predicate: (IObjectRef) -> Boolean, func: (IObjectRef, IObject) -> IObject): IModel {
        return copy(
                objects = objects.mapIndexed { index, iObject ->
                    val ref = ObjectRef(index)
                    if (predicate(ref))
                        func(ref, iObject)
                    else iObject
                },
                materials = materials,
                visibilities = visibilities
        )
    }

    override fun addMaterial(material: IMaterial): IModel {
        return copy(materials = materials + material)
    }

    override fun modifyMaterial(ref: IMaterialRef, new: IMaterial): IModel {
        return copy(
                materials = materials.mapIndexed { index, iMaterial -> if (index == ref.materialIndex) new else iMaterial })
    }

    override fun removeMaterial(materialRef: IMaterialRef): IModel {
        return copy(
                materials = materials.filterIndexed { index, _ -> index == materialRef.materialIndex },
                objects = objects.map {
                    when {
                        it.material == materialRef -> it.transformer.withMaterial(it, MaterialRef(-1))
                        it.material > materialRef -> it.transformer.withMaterial(it,
                                MaterialRef(it.material.materialIndex - 1))
                        else -> it
                    }
                }
        )
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? Model)?.id
    }

    override fun hashCode(): Int {
        return id
    }
}