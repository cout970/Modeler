package com.cout970.modeler.util

import com.cout970.collision.IPolygon
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.collision.TexturePolygon
import com.cout970.modeler.core.model.mesh.getTextureVertex
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*

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

fun IMesh.toRayObstacle(): IRayObstacle {
    return object : IRayObstacle {
        override fun rayTrace(ray: Ray): RayTraceResult? = getHits(ray).getClosest(ray)
    }
}

fun IMesh.getFaceRayObstacles(objRef: IObjectRef): List<Pair<IRayObstacle, IRef>> {
    return faces.mapIndexed { ref, faceIndex ->
        object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? = getFaceHit(ray, faceIndex)
        } to objRef.toFaceRef(ref)
    }
}

fun IMesh.getEdgeRayObstacles(objRef: IObjectRef): List<Pair<IRayObstacle, IRef>> {
    return faces.flatMap { f ->

        (0 until f.vertexCount).map { index ->
            val next = (index + 1) % f.vertexCount

            object : IRayObstacle {
                override fun rayTrace(ray: Ray): RayTraceResult? = getEdgeHit(ray, f.pos[index], f.pos[next])
            } to objRef.toEdgeRef(f.pos[index], f.pos[next])
        }
    }
}

fun IMesh.getVertexRayObstacles(objRef: IObjectRef): List<Pair<IRayObstacle, IRef>> {
    return pos.mapIndexed { index, _ ->
        object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? = getVertexHit(ray, index)
        } to objRef.toPosRef(index)
    }
}


fun IObject.getTexturePolygon(objRef: IObjectRef): List<Pair<IPolygon, IRef>> =
        mesh.faces.mapIndexed { _, faceIndex ->
            TexturePolygon(faceIndex.getTextureVertex(mesh)) to objRef
        }

fun IObject.getFaceTexturePolygons(objRef: IObjectRef): List<Pair<IPolygon, IRef>> =
        mesh.faces.mapIndexed { ref, faceIndex ->
            TexturePolygon(faceIndex.getTextureVertex(mesh)) to objRef.toFaceRef(ref)
        }

fun IObject.getEdgeTexturePolygons(objRef: IObjectRef, material: IMaterial): List<Pair<IPolygon, IRef>> =
        mesh.faces.flatMap { f ->
            (0 until f.vertexCount).map { index ->
                val next = (index + 1) % f.vertexCount
                val a = f.tex[index]
                val b = f.tex[next]

                getEdgeTexturePolygon(mesh.tex[a], mesh.tex[b], material) to objRef.toEdgeRef(a, b)
            }
        }

fun IObject.getVertexTexturePolygons(objRef: IObjectRef, material: IMaterial): List<Pair<IPolygon, IRef>> =
        mesh.tex.mapIndexed { ref, coord ->
            getVertexTexturePolygon(coord, material) to objRef.toPosRef(ref)
        }

private fun getEdgeTexturePolygon(a: IVector2, b: IVector2, material: IMaterial): IPolygon {
    val AB = (b - a).normalize()
    val parallel = vec2Of(AB.yd, -AB.xd) // { y | -x }
    val axis = parallel * material.size.transform { 0.5 / it }

    return TexturePolygon(listOf(
            a - axis,
            a + axis,
            b + axis,
            b - axis
    ))
}

fun getVertexTexturePolygon(a: IVector2, material: IMaterial): IPolygon {
    val scale = material.size.transform { 0.25 / it }
    return TexturePolygon(listOf(
            a + vec2Of(-scale.xd, -scale.yd), a + vec2Of(scale.xd, -scale.yd),
            a + vec2Of(scale.xd, scale.yd), a + vec2Of(-scale.xd, scale.yd)
    ))
}
