package com.cout970.modeler.util

import com.cout970.glutilities.tessellator.ITessellator
import com.cout970.modeler.model.*
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/09.
 */
object RenderUtil {

    fun renderBar(tessellator: ITessellator, startPoint: IVector3, endPoint: IVector3, size: Double = 0.1,
                  color: IVector3 = vec3Of(1, 1, 0)) {

        val start = startPoint
        val end = endPoint

        val dir = (end - start).normalize()

        val q = Quaterniond().rotationTo(dir.toJoml3d(), Vector3d(1.0, 0.0, 0.0))
        val rot: IQuaternion = if (end != start) quatOf(q.x, q.y, q.z, q.w) else Quaternion.IDENTITY

        val matrix = Transformation(start, rot, vec3Of(1)).matrix
        val mesh = Meshes.createCube(vec3Of(start.distance(end) + size, size, size), offset = vec3Of(-size / 2))

        tessellator.apply {
            for ((pos) in mesh.getQuads().map { it.transform(matrix) }.flatMap(Quad::vertex)) {
                set(0, pos.x, pos.y, pos.z).set(1, color.x, color.y, color.z).endVertex()
            }
        }
    }

    fun renderCircle(t: ITessellator, center: IVector3, axis: SelectionAxis, radius: Double, size: Double = 0.05,
                     color: IVector3 = vec3Of(1, 1, 1)) {
        val quality = 16
        for (i in 0..360 / quality) {
            val angle0 = Math.toRadians(i.toDouble() * quality)
            val angle1 = Math.toRadians((i.toDouble() + 1) * quality)

            val start = if (axis == SelectionAxis.Y) {
                vec3Of(Math.sin(angle0), Math.cos(angle0), 0)
            } else if (axis == SelectionAxis.Z) {
                vec3Of(0, Math.sin(angle0), Math.cos(angle0))
            } else {
                vec3Of(Math.sin(angle0), 0, Math.cos(angle0))
            }

            val end = if (axis == SelectionAxis.Y) {
                vec3Of(Math.sin(angle1), Math.cos(angle1), 0)
            } else if (axis == SelectionAxis.Z) {
                vec3Of(0, Math.sin(angle1), Math.cos(angle1))
            } else {
                vec3Of(Math.sin(angle1), 0, Math.cos(angle1))
            }

            renderBar(t, start * radius + center, end * radius + center, size, color)
        }
    }

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

    fun zipVertexPaths(model: Model, paths: List<VertexPath>): VertexStructurePath {
        val quadList = mutableListOf<List<VertexPath>>()
        val edgeList = mutableListOf<Pair<VertexPath, VertexPath>>()
        val map = paths.groupBy { model.getElement(it.getParent()!!) as IElementObject }

        // Get all quads
        for ((elem, vertex) in map) {
            val indices = vertex.map { it.vertexIndex }
            val facesWithAllVertexInIndices = elem.faces.filter { it.indices.all { it in indices } }
            facesWithAllVertexInIndices.forEach { face ->
                quadList += face.indices.map { VertexPath(vertex[0].indices, it) }
            }
        }

        val vertexInQuads = quadList.flatMap { it }
        //Get all edges
        for ((elem, vertex) in map) {
            val indices = vertex.filter { it !in vertexInQuads }.map { it.vertexIndex }
            val edges = elem.faces.flatMap { listOf(it.a to it.b, it.b to it.c, it.c to it.d, it.d to it.a) }
            println(edges.size)
            val edgesWithAllVertexInIndices = edges.filter { it.first in indices && it.second in indices }
            edgesWithAllVertexInIndices.forEach { edge ->
                val a = vertex.find { it.vertexIndex == edge.first }!!
                val b = vertex.find { it.vertexIndex == edge.second }!!
                edgeList += (a to b)
            }
        }

        // Get remaining vertex
        val vertexInQuadsOrEdges = vertexInQuads + edgeList.flatMap { listOf(it.first, it.second) }
        val vertexList = paths.filter { it !in vertexInQuadsOrEdges }

        return VertexStructurePath(
                quadList,
                edgeList,
                vertexList
        )
    }
}