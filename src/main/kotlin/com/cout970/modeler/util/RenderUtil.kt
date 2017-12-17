package com.cout970.modeler.util

import com.cout970.collision.IPolygon
import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.collision.TexturePolygon
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.render.tool.append
import com.cout970.vector.api.IVector2
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

    fun appendBar(buffer: BufferPTNC, startPoint: IVector3, endPoint: IVector3,
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
                    pos
                            .map { matrix.transform(Vector4d(it.xd, it.yd, it.zd, 1.0)) }
                            .map { vec3Of(it.x, it.y, it.z) },
                    tex,
                    faces)
        }
    }

    fun test(q: Quaterniond): IVector3 {
        return q.transform(Vector3d(1.0, 0.0, 0.0)).toIVector()
    }

    fun createCirclePolygons(center: IVector2, radius: Double, size: Double = 0.05): List<IPolygon> {

        val quality = 16
        val polygons = mutableListOf<IPolygon>()
        for (i in 0..360 / quality) {
            val angle0 = Math.toRadians(i.toDouble() * quality)
            val angle1 = Math.toRadians((i.toDouble() + 1) * quality)

            val halfSize = size / 2

            val near1 = vec2Of(Math.sin(angle0), Math.cos(angle0)) * (radius + halfSize)
            val near2 = vec2Of(Math.sin(angle1), Math.cos(angle1)) * (radius + halfSize)
            val far1 = vec2Of(Math.sin(angle0), Math.cos(angle0)) * (radius - halfSize)
            val far2 = vec2Of(Math.sin(angle1), Math.cos(angle1)) * (radius - halfSize)

            val points = listOf(near1, near2, far2, far1).map { it + center }

            polygons.add(TexturePolygon(points))
        }
        return polygons
    }

    fun createCircleMesh(center: IVector3, axis: IVector3, radius: Double, size: Double = 0.05): IMesh {

        val quality = 16
        val meshes = mutableListOf<IMesh>()
        for (i in 0..360 / quality) {
            val angle0 = Math.toRadians(i.toDouble() * quality)
            val angle1 = Math.toRadians((i.toDouble() + 1) * quality)

            val (axis0, axis1) = axis.getPerpendicularPlane()

            val start = axis0 * Math.sin(angle0) + axis1 * Math.cos(angle0)
            val end = axis0 * Math.sin(angle1) + axis1 * Math.cos(angle1)

            meshes += createBarMesh(start * radius + center, end * radius + center, size)
        }
        return if (meshes.isEmpty()) Mesh() else meshes.reduce { acc, mesh -> acc.merge(mesh) }
    }

    fun appendAABB(buffer: BufferPTNC, box: AABB, color: IVector3 = vec3Of(1, 1, 1)) {

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