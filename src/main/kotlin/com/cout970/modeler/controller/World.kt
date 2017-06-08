package com.cout970.modeler.controller

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.IModel

/**
 * Created by cout970 on 2017/06/08.
 */
data class World(val models: List<IModel>) {

    val cache: MutableList<List<VAO>> = models.map { listOf<VAO>() }.toMutableList()
}