package com.cout970.modeler.core.model.material

import com.cout970.modeler.api.model.material.IMaterialRef

/**
 * Created by cout970 on 2017/07/09.
 */
data class MaterialRef(override val materialIndex: Int) : IMaterialRef {

    override fun equals(other: Any?): Boolean {
        return other is IMaterialRef && other.materialIndex == materialIndex
    }

    override fun hashCode(): Int {
        return materialIndex
    }
}

object MaterialRefNone : IMaterialRef {

    override val materialIndex: Int = -1

    override fun equals(other: Any?): Boolean {
        return other is IMaterialRef && other.materialIndex == materialIndex
    }

    override fun hashCode(): Int {
        return materialIndex
    }

    override fun toString(): String {
        return "MaterialRef(None)"
    }
}