package com.cout970.modeler.modelcontrol.selection

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.model.*
import com.cout970.modeler.util.center
import com.cout970.modeler.util.middle
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
data class ModelPath(
        val obj: Int,
        val group: Int = -1,
        val mesh: Int = -1,
        val quad: Int = -1,
        val vertex: Int = -1) {

    fun getObject(model: Model) = if (obj == -1) null else model.objects[obj]
    fun getGroup(model: Model) = if (group == -1) null else getObject(model)?.groups?.get(group)
    fun getMesh(model: Model) = if (mesh == -1) null else getGroup(model)?.meshes?.get(mesh)
    fun getQuad(model: Model) = if (quad == -1) null else getMesh(model)?.getQuads()?.get(quad)
    fun getVertex(model: Model) = if (vertex == -1) null else getMesh(model)?.positions?.get(vertex)

    fun compareLevel(other: ModelPath, level: Level): Boolean {
        return when (level) {
            ModelPath.Level.OBJECTS -> obj == other.obj
            ModelPath.Level.GROUPS -> obj == other.obj && group == other.group
            ModelPath.Level.COMPONENTS -> obj == other.obj && group == other.group && mesh == other.mesh
            ModelPath.Level.QUADS -> obj == other.obj && group == other.group && mesh == other.mesh && quad == other.quad
            ModelPath.Level.VERTEX -> obj == other.obj && group == other.group && mesh == other.mesh && quad == other.quad && vertex == other.vertex
        }
    }

    val level: Level by lazy {
        if (group == -1) Level.OBJECTS
        else if (mesh == -1) Level.GROUPS
        else if (quad == -1) Level.COMPONENTS
        else if (vertex == -1) Level.QUADS
        else Level.VERTEX
    }

    fun getCenter(model: Model): IVector3 {
        return when (level) {
            Level.OBJECTS -> getObject(model)!!.transform.position
            Level.GROUPS -> getObject(model)!!.transform.position + getGroup(model)!!.transform.position
            Level.COMPONENTS -> getObject(model)!!.transform.position + getGroup(model)!!.transform.position + getMesh(model)!!.transform.position + getMesh(model)!!.getQuads().map(Quad::center).middle()
            Level.QUADS -> getObject(model)!!.transform.position + getGroup(model)!!.transform.position + getMesh(model)!!.transform.position + getQuad(model)!!.center()
            Level.VERTEX -> getObject(model)!!.transform.position + getGroup(model)!!.transform.position + getMesh(model)!!.transform.position + getVertex(model)!!
            else -> vec3Of(0)
        }
    }

    override fun toString(): String {
        return "ModelPath(obj=$obj, group=$group, component=$mesh, quad=$quad, vertex=$vertex)"
    }

    companion object {
        fun of(model: Model, obj: ModelObject? = null, group: ModelGroup? = null, mesh: Mesh? = null, indexQuad: Int = -1, indexVertex: Int = -1): ModelPath {
            var indexObj = -1
            var indexGroup = -1
            var indexComponent = -1

            if (obj != null) {
                indexObj = model.objects.indexOf(obj)
                if (indexObj != -1 && group != null) {
                    indexGroup = obj.groups.indexOf(group)
                    if (indexGroup != -1 && mesh != null) {
                        indexComponent = group.meshes.indexOf(mesh)
                    }
                }
            }
            return ModelPath(indexObj, indexGroup, indexComponent, indexQuad, indexVertex)
        }
    }

    enum class Level {
        OBJECTS,
        GROUPS,
        COMPONENTS,
        QUADS,
        VERTEX
    }

    fun getComponentMatrix(model: Model): IMatrix4 {
        return getObject(model)!!.transform.matrix * getGroup(model)!!.transform.matrix * getMesh(model)!!.transform.matrix
    }

    fun getSubPaths(model: Model): List<ModelPath> {
        return when (level) {
            ModelPath.Level.OBJECTS -> {
                getObject(model)!!.groups.mapIndexed { i, _ -> ModelPath(obj, i) }
            }
            ModelPath.Level.GROUPS -> {
                getGroup(model)!!.meshes.mapIndexed { i, _ -> ModelPath(obj, group, i) }
            }
            ModelPath.Level.COMPONENTS -> {
                getMesh(model)!!.getQuads().mapIndexed { i, _ -> ModelPath(obj, group, mesh, i) }
            }
            ModelPath.Level.QUADS -> {
                getQuad(model)!!.vertex.mapIndexed { i, _ -> ModelPath(obj, group, mesh, quad, i) }
            }
            ModelPath.Level.VERTEX -> listOf()
        }
    }
}