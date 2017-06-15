package com.cout970.modeler.util

import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.view.render.tool.append
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector4d


/**
 * Created by cout970 on 2016/12/09.
 */
object RenderUtil {

    fun appendBar(buffer: UniversalShader.Buffer, startPoint: IVector3, endPoint: IVector3,
                  size: Double = 0.1, color: IVector3 = vec3Of(1, 1, 0)) {

        createBarMesh(startPoint, endPoint, size).append(buffer, color)
    }

    fun createBarMesh(startPoint: IVector3, endPoint: IVector3, size: Double = 0.1): IMesh {

        val start = startPoint
        val end = endPoint

        val dir = (end - start).normalize()

        val rotation = if (end != start)
            Quaterniond().rotationTo(Vector3d(1.0, 0.0, 0.0), dir.toJoml3d())
        else
            Quaterniond()

        val mesh = MeshFactory.createCube(
                size = vec3Of(start.distance(end) + size, size, size),
                offset = vec3Of(-size / 2)
        )

        val matrix = Matrix4d().apply {
            translate(start.toJoml3d())
            rotate(rotation)
        }

        return mesh.run {
            Mesh(
                    pos.map {
                        matrix.transform(Vector4d(it.xd, it.yd, it.zd, 1.0)).toIVector()
                    },
                    tex,
                    faces)
        }
    }

    fun test(q: Quaterniond): IVector3 {
        return q.transform(Vector3d(1.0, 0.0, 0.0)).toIVector()
    }

//    fun renderCircle(t: ITessellator, center: IVector3, axis: IVector3, radius: Double, size: Double = 0.05,
//                     color: IVector3 = vec3Of(1, 1, 1)) {
//        val quality = 16
//        for (i in 0..360 / quality) {
//            val angle0 = Math.toRadians(i.toDouble() * quality)
//            val angle1 = Math.toRadians((i.toDouble() + 1) * quality)
//
//            val (axis0, axis1) = axis.getPerpendicularPlane()
//
//            val start = axis0 * Math.sin(angle0) + axis1 * Math.cos(angle0)
//            val end = axis0 * Math.sin(angle1) + axis1 * Math.cos(angle1)
//
//            renderBar(t, start * radius + center, end * radius + center, size, color)
//        }
//    }

    fun appendAABB(buffer: UniversalShader.Buffer, box: AABB, color: IVector3 = vec3Of(1, 1, 1)) {

        buffer.apply {
            add(vec3Of(box.minX, box.minY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.minY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.minY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.maxY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.minY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.minY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.maxY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.maxY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.maxY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.minY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.maxY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.maxY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.maxY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.maxY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.minY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.maxY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.maxY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.maxY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.minY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.minY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.maxY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.minX, box.minY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.minY, box.maxZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(box.maxX, box.minY, box.minZ), Vector2.ORIGIN, Vector3.ORIGIN, color)
        }
    }
}