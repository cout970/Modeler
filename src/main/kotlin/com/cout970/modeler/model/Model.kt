package com.cout970.modeler.model

import com.cout970.modeler.modelcontrol.selection.ModelPath
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode

/**
 * Created by cout970 on 2016/11/29.
 */

private var modelIds = 0

data class Model(val objects: List<ModelObject>, val id: Int = modelIds++) {

    val quads: List<Quad> by lazy {
        getPaths(ModelPath.Level.COMPONENTS).flatMap { path ->
            path.getMesh(this)!!.getQuads().map { it.transform(path.getComponentMatrix(this)) }
        }
    }

    fun copy(objects: List<ModelObject> = this.objects): Model {
        return Model(objects)
    }

    fun getGroups() = objects.map { it.groups }.flatten()

    fun getComponents() = objects.flatMap { it.getComponents() }

    fun getPaths(level: ModelPath.Level): List<ModelPath> {
        return when (level) {
            ModelPath.Level.OBJECTS -> objects.map { ModelPath.of(this, it) }

            ModelPath.Level.GROUPS -> objects.flatMap { obj -> obj.groups.map { ModelPath.of(this, obj, it) } }

            ModelPath.Level.COMPONENTS -> objects.flatMap { obj ->
                obj.groups.flatMap { group ->
                    group.meshes.map { ModelPath.of(this, obj, group, it) }
                }
            }

            ModelPath.Level.QUADS -> objects.flatMap { obj ->
                obj.groups.flatMap { group ->
                    group.meshes.flatMap { comp -> comp.getQuads().mapIndexed { i, quad -> ModelPath.of(this, obj, group, comp, i) } }
                }
            }

            ModelPath.Level.VERTEX -> objects.flatMap { obj ->
                obj.groups.flatMap { group ->
                    group.meshes.flatMap { comp ->
                        var quadIndex = 0
                        comp.indices.flatMap { quad ->
                            quadIndex++
                            listOf(ModelPath.of(this, obj, group, comp, quadIndex, quad.aP),
                                    ModelPath.of(this, obj, group, comp, quadIndex, quad.bP),
                                    ModelPath.of(this, obj, group, comp, quadIndex, quad.cP),
                                    ModelPath.of(this, obj, group, comp, quadIndex, quad.dP))
                        }
                    }
                }
            }
        }
    }

    fun add(obj: ModelObject): Model = copy(objects + obj)

    fun getQuadsOptimized(selection: Selection, func: (Quad) -> Unit) {
        when (selection.mode) {
            SelectionMode.GROUP -> getPaths(ModelPath.Level.GROUPS).filter { selection.isSelected(it) }.flatMap { group ->
                group.getSubPaths(this)
            }.flatMap { path ->
                path.getMesh(this)!!.getQuads().map { it.transform(path.getComponentMatrix(this)) }
            }
            SelectionMode.COMPONENT -> getPaths(ModelPath.Level.COMPONENTS).filter { selection.isSelected(it) }.flatMap { path ->
                path.getMesh(this)!!.getQuads().map { it.transform(path.getComponentMatrix(this)) }
            }
            SelectionMode.QUAD -> {
                getPaths(ModelPath.Level.COMPONENTS).filter { selection.containsSelectedElements(it) }.flatMap { comp ->
                    val matrix = comp.getComponentMatrix(this)
                    comp.getSubPaths(this).filter { selection.isSelected(it) }.map { it.getQuad(this)!!.transform(matrix) }
                }
            }
            else -> {
                listOf<Quad>()
            }
        }.forEach(func)
    }
}

data class ModelObject(val groups: List<ModelGroup>, val transform: Transformation, val name: String, val material: Material) {

    fun getComponents() = groups.map { it.meshes }.flatten()

    fun add(group: ModelGroup): ModelObject = copy(groups + group)
}

data class ModelGroup(val meshes: List<Mesh>, val transform: Transformation, val name: String) {

    fun getQuads() = meshes.flatMap(Mesh::getQuads)

    fun add(comp: Mesh): ModelGroup = copy(meshes + comp)
}

sealed class Material(val name: String) {

    class TexturedMaterial(texture: String) : Material(texture)

    object MaterialNone : Material("noTexture")
}