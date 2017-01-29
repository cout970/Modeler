package com.cout970.modeler.modeleditor.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.util.middle
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector2

/**
 * Created by cout970 on 2016/12/07.
 */

interface ISelection {
    val paths: List<ModelPath>

    fun isSelected(path: ModelPath): Boolean
    fun containsSelectedElements(path: ModelPath): Boolean
}

interface IModelSelection : ISelection {
    override val paths: List<ModelPath>
    val modelMode: ModelSelectionMode

    fun getCenter3D(model: Model): IVector3
    fun toTextureSelection(model: Model): ITextureSelection
}

interface ITextureSelection : ISelection {
    override val paths: List<ModelPath>
    val textureMode: TextureSelectionMode

    fun getCenter2D(model: Model): IVector2
}

abstract class Selection : IModelSelection {

    abstract override val modelMode: ModelSelectionMode
    abstract override val paths: List<ModelPath>

    override fun getCenter3D(model: Model) = paths.map { it.getModelCenter(model) }.middle()

    override fun isSelected(path: ModelPath): Boolean {
        return paths.any { it == path }
    }

    override fun containsSelectedElements(path: ModelPath): Boolean {
        return paths.any { it.compareLevel(path, path.level) }
    }
}

object SelectionNone : Selection(), ITextureSelection {

    override val modelMode: ModelSelectionMode = ModelSelectionMode.GROUP
    override val textureMode: TextureSelectionMode = TextureSelectionMode.QUAD
    override val paths: List<ModelPath> = emptyList()

    override fun getCenter2D(model: Model): IVector2 = Vector2.ORIGIN
    override fun toTextureSelection(model: Model): ITextureSelection = this
}

data class SelectionGroup(val group: List<ModelPath>) : Selection() {

    override val modelMode: ModelSelectionMode = ModelSelectionMode.GROUP
    override val paths: List<ModelPath> get() = group
    override fun toTextureSelection(model: Model): ITextureSelection {
        return SelectionMesh(paths.flatMap { it.getSubPaths(model) }).toTextureSelection(model)
    }
}

data class SelectionMesh(val meshes: List<ModelPath>) : Selection() {

    override val modelMode: ModelSelectionMode = ModelSelectionMode.MESH
    override val paths: List<ModelPath> get() = meshes
    override fun toTextureSelection(model: Model): ITextureSelection {
        return SelectionQuad(paths.flatMap { it.getSubPaths(model) })
    }
}

data class SelectionQuad(val quads: List<ModelPath>) : Selection(), ITextureSelection {

    override val modelMode: ModelSelectionMode = ModelSelectionMode.QUAD
    override val textureMode: TextureSelectionMode = TextureSelectionMode.QUAD
    override val paths: List<ModelPath> get() = quads

    override fun getCenter2D(model: Model): IVector2 = paths.map { it.getTextureCenter(model) }.middle()
    override fun toTextureSelection(model: Model): ITextureSelection = this
}

data class SelectionVertex(val vertex: List<ModelPath>) : Selection(), ITextureSelection {

    override val modelMode: ModelSelectionMode = ModelSelectionMode.VERTEX
    override val textureMode: TextureSelectionMode = TextureSelectionMode.VERTEX
    override val paths: List<ModelPath> get() = vertex

    override fun getCenter2D(model: Model): IVector2 = paths.map { it.getTextureCenter(model) }.middle()
    override fun toTextureSelection(model: Model): ITextureSelection = SelectionNone
}