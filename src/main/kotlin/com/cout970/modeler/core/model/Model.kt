package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.selection.ObjectRef

/**
 * Created by cout970 on 2017/05/07.
 */
data class Model(
        override val objectMap: Map<IObjectRef, IObject>,
        override val materialMap: Map<IMaterialRef, IMaterial>,
        override val visibilities: Map<IObjectRef, Boolean>
) : IModel {

    val id: Int = lastId++

    companion object {
        private var lastId = 0

        fun of(objects: List<IObject>, materials: List<IMaterial>): IModel {
            return Model(objects.associateBy { it.ref }, materials.associateBy { it.ref }, objects.associate { it.ref to true })
        }

        fun of(objects: Map<IObjectRef, IObject>, materials: List<IMaterial>): IModel {
            return Model(objects, materials.associateBy { it.ref }, objects.mapValues { true })
        }

        fun empty() = Model(emptyMap(), emptyMap(), emptyMap())
    }

    private constructor() : this(emptyMap(), emptyMap(), emptyMap())

    override fun isVisible(ref: IObjectRef): Boolean {
        if (ref in objectMap) {
            return visibilities[ref]!!
        }
        return false
    }

    override fun setVisible(ref: IObjectRef, visible: Boolean): IModel {
        return copy(
                visibilities = visibilities.mapValues { (index, value) ->
                    if (index == ref) visible else value
                }
        )
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

    override fun addObjects(objs: List<IObject>): IModel {
        return copy(
                objectMap = objectMap + objs.map { it.toPair() },
                materialMap = materialMap,
                visibilities = visibilities + objs.map { it.ref to true }
        )
    }

    override fun removeObjects(objs: List<IObjectRef>): IModel {
        val toRemove = objs.map { ObjectRef(it.objectId) }.toSet()
        return copy(
                objectMap = objectMap.filter { (index, _) -> index !in toRemove },
                materialMap = materialMap,
                visibilities = visibilities.filter { (index, _) -> index !in toRemove }
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
                materialMap = materialMap,
                visibilities = visibilities
        )
    }

    override fun addMaterial(material: IMaterial): IModel {
        return copy(materialMap = materialMap + (MaterialRef(material.id) to material))
    }

    override fun modifyMaterial(ref: IMaterialRef, new: IMaterial): IModel {
        return copy(
                materialMap = materialMap.toMutableMap().apply { put(ref, new) })
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

    override fun merge(other: IModel): IModel {

        val otherObjects = other.objects

        val materials = this.materialMap + other.materialMap
        val visibilities = this.visibilities + other.visibilities
        val objects = this.objectMap + otherObjects.associateBy { it.ref }

        return Model(
                objectMap = objects,
                materialMap = materials,
                visibilities = visibilities
        )
    }

    override fun compareTo(other: IModel): Int {
        if (this.objectMap != other.objectMap) return -1
        if (this.visibilities != other.visibilities) return -1
        if (this.materials != other.materials) return -1

        return 0
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? Model)?.id
    }

    override fun hashCode(): Int {
        return id
    }
}