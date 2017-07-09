package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.util.FakeRayObstacle
import com.cout970.modeler.util.getClosest
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil

/**
 * Created by cout970 on 2017/06/12.
 */
object RayTracer {

    fun getObject(ray: Ray, model: IModel): Pair<RayTraceResult, IObject>? {
        val hits = mutableListOf<Pair<RayTraceResult, IObject>>()

        model.objects.forEach { obj ->
            getHits(ray, obj).forEach {
                hits += it to obj
            }
        }
        return hits.getClosest(ray)
    }

    fun getHits(ray: Ray, obj: IObject): List<RayTraceResult> {
        val list = mutableListOf<RayTraceResult>()
        val mesh = obj.transformedMesh

        mesh.faces.forEach { face ->
            val a = mesh.pos[face.pos[0]]
            val b = mesh.pos[face.pos[1]]
            val c = mesh.pos[face.pos[2]]
            val d = mesh.pos[face.pos[3]]
            RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle, a, b, c, d)?.let {
                list += it
            }
        }
        return list
    }

    fun toRayObstacle(obj: IObject): IRayObstacle {
        return object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? = getHits(ray, obj).getClosest(ray)
        }
    }
}