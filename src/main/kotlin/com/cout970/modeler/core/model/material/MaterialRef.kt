package com.cout970.modeler.core.model.material

import com.cout970.modeler.api.model.material.IMaterialRef
import java.util.*

/**
 * Created by cout970 on 2017/07/09.
 */
data class MaterialRef(override val materialId: UUID) : IMaterialRef

object MaterialRefNone : IMaterialRef {

    override val materialId = MaterialNone.id

    override fun equals(other: Any?): Boolean {
        return other is IMaterialRef && other === this
    }

    override fun hashCode(): Int {
        return -1
    }

    override fun toString(): String {
        return "MaterialRef(None)"
    }
}