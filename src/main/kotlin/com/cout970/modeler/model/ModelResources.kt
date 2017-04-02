package com.cout970.modeler.model

import com.cout970.modeler.model.material.IMaterial
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.selection.ElementPath

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