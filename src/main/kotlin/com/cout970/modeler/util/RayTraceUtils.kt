package com.cout970.modeler.util

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil

/**
 * Created by cout970 on 2017/07/22.
 */

fun IModel.getObject(ray: Ray): Pair<RayTraceResult, IObject>? {
    val hits = mutableListOf<Pair<RayTraceResult, IObject>>()

    objects.forEach { obj ->
        obj.getHits(ray).forEach {
            hits += it to obj
        }
    }
    return hits.getClosest(ray)
}

fun IObject.getHits(ray: Ray): List<RayTraceResult> = mesh.getHits(ray)

fun IMesh.getHits(ray: Ray): List<RayTraceResult> {
    val list = mutableListOf<RayTraceResult>()

    faces.forEach { face ->
        val a = pos[face.pos[0]]
        val b = pos[face.pos[1]]
        val c = pos[face.pos[2]]
        val d = pos[face.pos[3]]
        RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle, a, b, c, d)?.let {
            list += it
        }
    }
    return list
}

fun IObject.toRayObstacle(): IRayObstacle {
    return object : IRayObstacle {
        override fun rayTrace(ray: Ray): RayTraceResult? = getHits(ray).getClosest(ray)
    }
}