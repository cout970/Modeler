package com.cout970.modeler.modeleditor.selection

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.util.center
import com.cout970.modeler.util.middle
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
data class ModelPath(
        val group: Int,
        val mesh: Int = -1,
        val quad: Int = -1,
        val vertex: Int = -1) {

    fun getGroup(model: Model) = if (group in model.groups.indices) model.groups[group] else null
    fun getMesh(model: Model) = if (mesh == -1) null else getGroup(model)?.meshes?.get(mesh)
    fun getQuad(model: Model) = if (quad == -1) null else getMesh(model)?.getQuads()?.get(quad)
    fun getVertex(model: Model) = if (vertex == -1) null else getMesh(model)?.positions?.get(vertex)

    fun compareLevel(other: ModelPath, level: Level): Boolean {
        return when (level) {
            ModelPath.Level.GROUPS -> group == other.group
            ModelPath.Level.MESH -> group == other.group && mesh == other.mesh
            ModelPath.Level.QUADS -> group == other.group && mesh == other.mesh && quad == other.quad
            ModelPath.Level.VERTEX -> group == other.group && mesh == other.mesh && quad == other.quad && vertex == other.vertex
        }
    }

    val level: Level by lazy {
        if (mesh == -1) Level.GROUPS
        else if (quad == -1) Level.MESH
        else if (vertex == -1) Level.QUADS
        else Level.VERTEX
    }

    fun getCenter(model: Model): IVector3 {
        return when (level) {
            Level.GROUPS -> getGroup(model)!!.transform.position
            Level.MESH -> getGroup(model)!!.transform.position + getMesh(model)!!.getQuads().map(Quad::center).middle()
            Level.QUADS -> getGroup(model)!!.transform.position + getQuad(model)!!.center()
            Level.VERTEX -> getGroup(model)!!.transform.position + getVertex(model)!!
            else -> vec3Of(0)
        }
    }

    override fun toString(): String {
        return "ModelPath(group=$group, mesh=$mesh, quad=$quad, vertex=$vertex)"
    }

    enum class Level {
        GROUPS,
        MESH,
        QUADS,
        VERTEX
    }

    fun getMeshMatrix(model: Model): IMatrix4 {
        return getGroup(model)!!.transform.matrix
    }

    fun getSubPaths(model: Model): List<ModelPath> {
        return when (level) {
            ModelPath.Level.GROUPS -> {
                getGroup(model)!!.meshes.mapIndexed { i, _ -> ModelPath(group, i) }
            }
            ModelPath.Level.MESH -> {
                getMesh(model)!!.getQuads().mapIndexed { i, _ -> ModelPath(group, mesh, i) }
            }
            ModelPath.Level.QUADS -> {
                getQuad(model)!!.vertex.mapIndexed { i, _ -> ModelPath(group, mesh, quad, i) }
            }
            ModelPath.Level.VERTEX -> listOf()
        }
    }

    fun getParent(): ModelPath {
        return when (level) {
            ModelPath.Level.GROUPS -> throw IllegalStateException("Path ($this) doesn't have a parent")
            ModelPath.Level.MESH -> ModelPath(group)
            ModelPath.Level.QUADS -> ModelPath(group, mesh)
            ModelPath.Level.VERTEX -> ModelPath(group, mesh, quad)
        }
    }
}