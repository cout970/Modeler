package com.cout970.modeler.view.controller

import com.cout970.raytrace.Ray
import com.cout970.vector.api.IVector2
import org.joml.Matrix4d

class SceneSpaceContext(val mousePos: IVector2, val mouseRay: Ray, val mvpMatrix: Matrix4d)