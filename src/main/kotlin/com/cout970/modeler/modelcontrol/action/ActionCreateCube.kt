package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.Cube
import com.cout970.modeler.modelcontrol.ModelPath
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
data class ActionCreateCube(val path: ModelPath) : IAction {

    val cube = Cube.create(vec3Of(1))

    override fun run() {
        if (cube !in path.group!!.components) {
            path.group.components += cube
        }
    }

    override fun undo() {
        path.group!!.components -= cube
    }
}