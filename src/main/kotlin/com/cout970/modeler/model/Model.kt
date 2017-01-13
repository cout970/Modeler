package com.cout970.modeler.model

import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.util.flatMapIndexed
import com.google.gson.annotations.Expose

/**
 * Created by cout970 on 2016/11/29.
 */

// the id is used to get a different hashCode for every model, so this can be used to detect changes
private var modelIds = 0

data class Model(@Expose val groups: List<ModelGroup>, val id: Int = modelIds++) {

    val quads: List<Quad> by lazy {
        getPaths(ModelPath.Level.MESH).flatMap { path ->
            path.getMesh(this)!!.getQuads().map { it.transform(path.getMeshMatrix(this)) }
        }
    }

    //copies the model with a different modelId so the hashCode of the model is different
    fun copy(groups: List<ModelGroup> = this.groups): Model {
        return Model(groups)
    }

    fun getMeshes() = groups.map { it.meshes }

    fun getPaths(level: ModelPath.Level): List<ModelPath> {
        return when (level) {
            ModelPath.Level.GROUPS -> groups.mapIndexed { groupIndex, _ ->
                ModelPath(groupIndex)
            }
            ModelPath.Level.MESH -> groups.flatMapIndexed { groupIndex, group ->
                group.meshes.mapIndexed { meshIndex, _ -> ModelPath(groupIndex, meshIndex) }
            }
            ModelPath.Level.QUADS -> groups.flatMapIndexed { groupIndex, group ->
                group.meshes.flatMapIndexed { meshIndex, mesh ->
                    mesh.getQuads().mapIndexed { quadIndex, quad ->
                        ModelPath(groupIndex, meshIndex, quadIndex)
                    }
                }
            }

            ModelPath.Level.VERTEX -> groups.flatMapIndexed { groupIndex, group ->
                group.meshes.flatMapIndexed { meshIndex, mesh ->
                    var quadIndex = 0
                    mesh.indices.flatMap { quad ->
                        quadIndex++
                        listOf(ModelPath(groupIndex, meshIndex, quadIndex, quad.aP),
                                ModelPath(groupIndex, meshIndex, quadIndex, quad.bP),
                                ModelPath(groupIndex, meshIndex, quadIndex, quad.cP),
                                ModelPath(groupIndex, meshIndex, quadIndex, quad.dP))
                    }
                }
            }
        }
    }

    fun getQuadsOptimized(selection: Selection, func: (Quad) -> Unit) {
        when (selection.mode) {
            SelectionMode.GROUP -> getPaths(ModelPath.Level.GROUPS).filter {
                selection.isSelected(it)
            }.flatMap { group ->
                group.getSubPaths(this)
            }.flatMap { path ->
                path.getMesh(this)!!.getQuads().map { it.transform(path.getMeshMatrix(this)) }
            }
            SelectionMode.MESH -> getPaths(ModelPath.Level.MESH).filter { selection.isSelected(it) }.flatMap { path ->
                path.getMesh(this)!!.getQuads().map { it.transform(path.getMeshMatrix(this)) }
            }
            SelectionMode.QUAD -> {
                getPaths(ModelPath.Level.MESH).filter { selection.containsSelectedElements(it) }.flatMap { mesh ->
                    val matrix = mesh.getMeshMatrix(this)
                    mesh.getSubPaths(this).filter { selection.isSelected(it) }.map {
                        it.getQuad(this)!!.transform(matrix)
                    }
                }
            }
            else -> {
                listOf<Quad>()
            }
        }.forEach(func)
    }
}

data class ModelGroup(@Expose val meshes: List<Mesh>,
                      @Expose val transform: Transformation = Transformation.IDENTITY,
                      @Expose val name: String,
                      @Expose val material: Material = MaterialNone) {

    fun getQuads() = meshes.flatMap(Mesh::getQuads)

    fun add(mesh: Mesh): ModelGroup = copy(meshes + mesh)

    fun addAll(meshes: List<Mesh>): ModelGroup = copy(this.meshes + meshes)
    override fun toString(): String {
        return "ModelGroup(${meshes.size} meshes, transform=$transform, name='$name', material=$material)"
    }


}