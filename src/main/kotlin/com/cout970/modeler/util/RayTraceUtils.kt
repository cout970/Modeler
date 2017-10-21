package com.cout970.modeler.util

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.IRef
import com.cout970.modeler.core.model.selection.EdgeRef
import com.cout970.modeler.core.model.selection.FaceRef
import com.cout970.modeler.core.model.selection.PosRef
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of

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

fun IMesh.getFirstHit(ray: Ray): RayTraceResult? {
    faces.forEach { face ->
        val a = pos[face.pos[0]]
        val b = pos[face.pos[1]]
        val c = pos[face.pos[2]]
        val d = pos[face.pos[3]]
        RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle, a, b, c, d)?.let {
            return it
        }
    }
    return null
}

fun IMesh.getFaceHit(ray: Ray, face: IFaceIndex): RayTraceResult? {
    val a = pos[face.pos[0]]
    val b = pos[face.pos[1]]
    val c = pos[face.pos[2]]
    val d = pos[face.pos[3]]
    return RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle, a, b, c, d)
}

fun IMesh.getEdgeHit(ray: Ray, a: Int, b: Int): RayTraceResult? =
        RenderUtil.createBarMesh(pos[a], pos[b], 0.5).getFirstHit(ray)

fun IMesh.getVertexHit(ray: Ray, a: Int): RayTraceResult? =
        RayTraceUtil.rayTraceBox3(pos[a] - vec3Of(0.25), pos[a] + vec3Of(0.25), ray, FakeRayObstacle)

fun IObject.toRayObstacle(): IRayObstacle {
    return object : IRayObstacle {
        override fun rayTrace(ray: Ray): RayTraceResult? = getHits(ray).getClosest(ray)
    }
}

fun IObject.getFaceRayObstacles(objRef: IObjectRef): List<Pair<IRayObstacle, IRef>> {
    return mesh.faces.mapIndexed { ref, faceIndex ->
        object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? = mesh.getFaceHit(ray, faceIndex)
        } to FaceRef(objRef.objectIndex, ref)
    }
}

fun IObject.getEdgeRayObstacles(objRef: IObjectRef): List<Pair<IRayObstacle, IRef>> {
    return mesh.faces.flatMapIndexed { _, f ->

        (0 until f.vertexCount).map { index ->
            val next = (index + 1) % f.vertexCount

            object : IRayObstacle {
                override fun rayTrace(ray: Ray): RayTraceResult? = mesh.getEdgeHit(ray, f.pos[index], f.pos[next])
            } to EdgeRef(objRef.objectIndex, f.pos[index], f.pos[next])
        }
    }
}

fun IObject.getVertexRayObstacles(objRef: IObjectRef): List<Pair<IRayObstacle, IRef>> {
    return mesh.pos.mapIndexed { index, _ ->
        object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? = mesh.getVertexHit(ray, index)
        } to PosRef(objRef.objectIndex, index)
    }
}