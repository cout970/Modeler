package com.cout970.modeler.util

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/01/26.
 */
enum class Direction(val axis: IVector3) {
    DOWN(vec3Of(0, -1, 0)),
    UP(vec3Of(0, 1, 0)),
    NORTH(vec3Of(0, 0, -1)),
    SOUTH(vec3Of(0, 0, 1)),
    WEST(vec3Of(-1, 0, 0)),
    EAST(vec3Of(1, 0, 0)),
}