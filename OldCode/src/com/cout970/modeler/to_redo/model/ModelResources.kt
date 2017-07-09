package com.cout970.modeler.to_redo.model

import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.to_redo.model.material.IMaterial
import com.cout970.modeler.to_redo.model.material.MaterialNone
import com.cout970.modeler.to_redo.selection.ElementPath

data class ModelResources(
        val materials: List<IMaterial> = listOf(),
        val pathToMaterial: Map<ElementPath, Int> = mapOf()
) {

    fun getMaterial(path: ElementPath): IMaterial {
        val index = pathToMaterial[path] ?: return MaterialNone
        return materials[index]
    }

    fun reloadResources(resourceLoader: ResourceLoader) {
        materials.distinct().forEach {
            it.loadTexture(resourceLoader)
        }
    }
}