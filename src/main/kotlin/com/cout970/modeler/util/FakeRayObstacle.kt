package com.cout970.modeler.util

import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult

/**
 * Created by cout970 on 2016/12/10.
 */
object FakeRayObstacle : IRayObstacle {
    override fun rayTrace(ray: Ray): RayTraceResult? = null
}