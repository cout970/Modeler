package com.cout970.modeler.gui.canvas

import com.cout970.raytrace.Ray
import com.cout970.vector.api.IVector2
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/06/15.
 */

data class SceneSpaceContext(val mousePos: IVector2, val mouseRay: Ray, val mvpMatrix: Matrix4d)
