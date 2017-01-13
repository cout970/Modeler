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

data class Model(@Expose val objects: List<ModelObject>, val id: Int = modelIds++) {

    val quads: List<Quad> by lazy {
        getPaths(ModelPath.Level.MESH).flatMap { path ->
            path.getMesh(this)!!.getQuads().map { it.transform(path.getMeshMatrix(this)) }
        }
    }

    fun copy(objects: List<ModelObject> = this.objects): Model {
        return Model(objects)
    }

    fun getGroups() = objects.map { it.groups }.flatten()

    fun getMeshes() = objects.flatMap { it.getMeshes() }

    fun getPaths(level: ModelPath.Level): List<ModelPath> {
        return when (level) {
            ModelPath.Level.OBJECTS -> objects.mapIndexed { index, _ -> ModelPath(index) }

            ModelPath.Level.GROUPS -> objects.flatMapIndexed { objIndex, obj ->
                obj.groups.mapIndexed { groupIndex, _ ->
                    ModelPath(objIndex, groupIndex)
                }
            }

            ModelPath.Level.MESH -> objects.flatMapIndexed { objIndex, obj ->
                obj.groups.flatMapIndexed { groupIndex, group ->
                    group.meshes.mapIndexed { meshIndex, _ -> ModelPath(objIndex, groupIndex, meshIndex) }
                }
            }

            ModelPath.Level.QUADS -> objects.flatMapIndexed { objIndex, obj ->
                obj.groups.flatMapIndexed { groupIndex, group ->
                    group.meshes.flatMapIndexed { meshIndex, mesh ->
                        mesh.getQuads().mapIndexed { quadIndex, quad ->
                            ModelPath(objIndex, groupIndex, meshIndex, quadIndex)
                        }
                    }
                }
            }

            ModelPath.Level.VERTEX -> objects.flatMapIndexed { objIndex, obj ->
                obj.groups.flatMapIndexed { groupIndex, group ->
                    group.meshes.flatMapIndexed { meshIndex, mesh ->
                        var quadIndex = 0
                        mesh.indices.flatMap { quad ->
                            quadIndex++
                            listOf(ModelPath(objIndex, groupIndex, meshIndex, quadIndex, quad.aP),
                                   ModelPath(objIndex, groupIndex, meshIndex, quadIndex, quad.bP),
                                   ModelPath(objIndex, groupIndex, meshIndex, quadIndex, quad.cP),
                                   ModelPath(objIndex, groupIndex, meshIndex, quadIndex, quad.dP))
                        }
                    }
                }
            }
        }
    }

    fun add(obj: ModelObject): Model = copy(objects + obj)

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

data class ModelObject(@Expose val groups: List<ModelGroup>,
                       @Expose val transform: Transformation = Transformation.IDENTITY,
                       @Expose val name: String,
                       @Expose val material: Material) {

    fun getMeshes() = groups.map { it.meshes }.flatten()

    fun add(group: ModelGroup): ModelObject = copy(groups + group)

    fun addAll(groups: List<ModelGroup>): ModelObject = copy(groups + groups)
}

data class ModelGroup(@Expose val meshes: List<Mesh>,
                      @Expose val transform: Transformation = Transformation.IDENTITY,
                      @Expose val name: String,
                      @Expose val material: Material = MaterialNone) {

    fun getQuads() = meshes.flatMap(Mesh::getQuads)

    fun add(mesh: Mesh): ModelGroup = copy(meshes + mesh)

    fun addAll(meshes: List<Mesh>): ModelGroup = copy(this.meshes + meshes)
}