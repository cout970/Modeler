package com.cout970.modeler.util

import com.cout970.glutilities.tessellator.ITessellator
import com.cout970.modeler.core.model.AABB

/**
 * Created by cout970 on 2016/12/09.
 */
object RenderUtil {

//    fun renderBar(tessellator: ITessellator, startPoint: IVector3, endPoint: IVector3, size: Double = 0.1,
//                  color: IVector3 = vec3Of(1, 1, 0)) {
//        renderBar(startPoint, endPoint, size) { pos ->
//            tessellator.apply {
//                set(0, pos.x, pos.y, pos.z).set(1, color.x, color.y, color.z).endVertex()
//            }
//        }
//    }

//    fun renderBar(startPoint: IVector3, endPoint: IVector3, size: Double = 0.1, renderFunc: (IVector3) -> Unit) {
//
//        val start = startPoint
//        val end = endPoint
//
//        val dir = (end - start).normalize()
//
//        val q = Quaterniond().rotationTo(dir.toJoml3d(), Vector3d(1.0, 0.0, 0.0))
//        val rot: IQuaternion = if (end != start) quatOf(q.x, q.y, q.z, q.w) else Quaternion.IDENTITY
//
//        val matrix = Transformation(start, rot, vec3Of(1)).matrix
//        val mesh = Meshes.createCube(vec3Of(start.distance(end) + size, size, size), offset = vec3Of(-size / 2))
//
//        for ((pos) in mesh.getQuads().map { it.transform(matrix) }.flatMap(Quad::vertex)) {
//            renderFunc(pos)
//        }
//    }

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

    fun renderBox(tes: ITessellator, box: AABB) {

        tes.apply {
            set(0, box.minX, box.minY, box.minZ).set(1, 1, 1, 1).endVertex()
            set(0, box.maxX, box.minY, box.minZ).set(1, 1, 1, 1).endVertex()

            set(0, box.minX, box.minY, box.minZ).set(1, 1, 1, 1).endVertex()
            set(0, box.minX, box.maxY, box.minZ).set(1, 1, 1, 1).endVertex()

            set(0, box.minX, box.minY, box.minZ).set(1, 1, 1, 1).endVertex()
            set(0, box.minX, box.minY, box.maxZ).set(1, 1, 1, 1).endVertex()

            set(0, box.maxX, box.maxY, box.maxZ).set(1, 1, 1, 1).endVertex()
            set(0, box.minX, box.maxY, box.maxZ).set(1, 1, 1, 1).endVertex()

            set(0, box.maxX, box.maxY, box.maxZ).set(1, 1, 1, 1).endVertex()
            set(0, box.maxX, box.minY, box.maxZ).set(1, 1, 1, 1).endVertex()

            set(0, box.maxX, box.maxY, box.maxZ).set(1, 1, 1, 1).endVertex()
            set(0, box.maxX, box.maxY, box.minZ).set(1, 1, 1, 1).endVertex()

            set(0, box.minX, box.maxY, box.minZ).set(1, 1, 1, 1).endVertex()
            set(0, box.maxX, box.maxY, box.minZ).set(1, 1, 1, 1).endVertex()

            set(0, box.maxX, box.minY, box.minZ).set(1, 1, 1, 1).endVertex()
            set(0, box.maxX, box.maxY, box.minZ).set(1, 1, 1, 1).endVertex()

            set(0, box.minX, box.maxY, box.minZ).set(1, 1, 1, 1).endVertex()
            set(0, box.minX, box.maxY, box.maxZ).set(1, 1, 1, 1).endVertex()

            set(0, box.maxX, box.minY, box.maxZ).set(1, 1, 1, 1).endVertex()
            set(0, box.minX, box.minY, box.maxZ).set(1, 1, 1, 1).endVertex()

            set(0, box.minX, box.maxY, box.maxZ).set(1, 1, 1, 1).endVertex()
            set(0, box.minX, box.minY, box.maxZ).set(1, 1, 1, 1).endVertex()

            set(0, box.maxX, box.minY, box.maxZ).set(1, 1, 1, 1).endVertex()
            set(0, box.maxX, box.minY, box.minZ).set(1, 1, 1, 1).endVertex()
        }
    }
}