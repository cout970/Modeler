package com.cout970.modeler.render.world

import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.edges
import com.cout970.modeler.core.model.faces
import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.core.model.pos
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.render.tool.*
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.getColor
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of
import org.lwjgl.opengl.ARBMultisample
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13

/**
 * Created by cout970 on 2017/06/09.
 */

class ModelRenderer {

    private var objectCacheHash: MutableMap<IObjectRef, Int> = mutableMapOf()
    private var objectCache: MutableMap<IObjectRef, ObjectCache> = mutableMapOf()
    private var modelHash = -1
    private var modelSelectionHash = -1
    private var textureSelectionHash = -1

    private data class ObjectCache(val geometry: VAO, val modelSelection: VAO?, val textureSelection: VAO?)

    fun renderModels(ctx: RenderContext, model: IModel) {

        val modelToRender = ctx.gui.state.tmpModel ?: model

        // Fixes white pixels in borders
        GL11.glDisable(ARBMultisample.GL_MULTISAMPLE_ARB)
        GL11.glDisable(GL13.GL_MULTISAMPLE)

        updateCache(ctx, modelToRender)
        renderModel(ctx, modelToRender)
    }

    fun updateCache(ctx: RenderContext, model: IModel) {

        val modelSel = ctx.gui.programState.modelSelectionHandler.getSelection()
        val textureSel = ctx.gui.programState.textureSelectionHandler.getSelection()

        if (model.hashCode() != modelHash || modelSel.hashCode() != modelSelectionHash || textureSel.hashCode() != textureSelectionHash) {
            modelHash = model.hashCode()
            modelSelectionHash = modelSel.hashCode()
            textureSelectionHash = textureSel.hashCode()

            val oldMap = objectCache.toMap()
            objectCache.clear()

            model.objectMap.values.filter { it.visible }.forEach { obj ->
                if (obj.ref in objectCacheHash) {
                    val oldHash = objectCacheHash[obj.ref]!!
                    val newHash = obj.hashCode() xor modelSelectionHash xor textureSelectionHash
                    if (oldHash == newHash) {
                        objectCache[obj.ref] = oldMap[obj.ref]!!
                        return@forEach
                    }
                }

                val geom = obj.mesh.createVao(ctx.buffer, getColor(obj.id.hashCode()))
                val modSel = getSelectionVao(ctx, obj, modelSel, Config.colorPalette.modelSelectionColor)
                val texSel = getSelectionVao(ctx, obj, textureSel, Config.colorPalette.textureSelectionColor)

                objectCache[obj.ref] = ObjectCache(geom, modSel, texSel)
                objectCacheHash[obj.ref] = obj.hashCode() xor modelSelectionHash xor textureSelectionHash
            }

            oldMap.forEach { ref, cache ->
                if (ref !in objectCache) {
                    cache.geometry.close()
                    cache.modelSelection?.close()
                    cache.textureSelection?.close()
                    objectCacheHash.remove(ref)
                } else if (objectCache[ref] != cache) {
                    cache.geometry.close()
                    cache.modelSelection?.close()
                    cache.textureSelection?.close()
                }
            }
        }
    }

    fun renderModel(ctx: RenderContext, model: IModel) {

        val matrixCache = mutableMapOf<IObjectRef, IMatrix4>()
        // Calculate matrix & animations

        val animation = ctx.gui.programState.animation
        val animator = ctx.gui.animator

        getRecursiveMatrix(matrixCache, model, animator, animation)

        //Render model
        objectCache.keys.groupBy { model.getObject(it).material }.forEach { materialRef, objs ->
            val material = model.getMaterial(materialRef).apply { bind() }

            ctx.shader.apply {
                useCubeMap.setBoolean(false)
                useTexture.setBoolean(ctx.gui.state.useTexture)
                useColor.setBoolean(ctx.gui.state.useColor)
                useLight.setBoolean(ctx.gui.state.useLight)
                showHiddenFaces.setBoolean(ctx.gui.state.showHiddenFaces)

                if (material is ColoredMaterial) {
                    useGlobalColor.setBoolean(true)
                    globalColor.setVector3(material.color)
                }

                objs.forEach objectLoop@{ objRef ->
                    matrixM.setMatrix4(matrixCache[objRef] ?: Matrix4.IDENTITY)
                    val cache = objectCache[objRef] ?: return@forEach
                    accept(cache.geometry)
                }

                showHiddenFaces.setBoolean(false)

                if (material is ColoredMaterial) {
                    globalColor.setVector3(Vector3.ONE)
                }
            }
        }

        // Outline
        if (ctx.gui.state.drawOutline) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
            GL11.glLineWidth(Config.selectionThickness * 10f)
            ctx.shader.apply {
                useCubeMap.setBoolean(false)
                useTexture.setBoolean(false)
                useColor.setBoolean(false)
                useLight.setBoolean(false)
                showHiddenFaces.setBoolean(false)
                useGlobalColor.setBoolean(true)
                globalColor.setVector3(vec3Of(0, 0, 0))

                objectCache.forEach { objRef, cache ->
                    matrixM.setMatrix4(matrixCache[objRef] ?: Matrix4.IDENTITY)
                    accept(cache.geometry)
                }

                globalColor.setVector3(Vector3.ONE)
                useGlobalColor.setBoolean(false)
                showHiddenFaces.setBoolean(false)
            }
            GL11.glLineWidth(1f)
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        }

        // Model Selection
        ctx.shader.apply {
            useTexture.setInt(0)
            useColor.setInt(1)
            useLight.setInt(0)
            GL11.glLineWidth(Config.selectionThickness * 20f)

            objectCache.forEach { objRef, cache ->
                val sel = cache.modelSelection ?: return@forEach
                matrixM.setMatrix4(matrixCache[objRef] ?: Matrix4.IDENTITY)
                accept(sel)
            }

            GL11.glLineWidth(1f)
        }

        // Texture Selection
        ctx.shader.apply {
            useTexture.setInt(0)
            useColor.setInt(1)
            useLight.setInt(0)
            GL11.glLineWidth(Config.selectionThickness * 20f)

            objectCache.forEach { objRef, cache ->
                val sel = cache.textureSelection ?: return@forEach
                matrixM.setMatrix4(matrixCache[objRef] ?: Matrix4.IDENTITY)
                accept(sel)
            }

            GL11.glLineWidth(1f)
        }
    }

    private fun getRecursiveMatrix(matrixCache: MutableMap<IObjectRef, IMatrix4>, model: IModel,
                                   animator: Animator, animation: IAnimation) {

        model.tree.objects[RootGroupRef].forEach { obj ->
            matrixCache[obj] = animator.animate(animation, obj, model.getObject(obj).transformation).matrix
        }

        model.tree.groups[RootGroupRef].forEach {
            getRecursiveMatrix(matrixCache, model, it, Matrix4.IDENTITY, animator, animation)
        }
    }

    private fun getRecursiveMatrix(matrixCache: MutableMap<IObjectRef, IMatrix4>, model: IModel,
                                   group: IGroupRef, matrix: IMatrix4, animator: Animator, animation: IAnimation) {

        val mat = animator.animate(animation, group, model.getGroup(group).transform).matrix * matrix

        model.tree.objects[group].forEach { obj ->
            matrixCache[obj] = mat * animator.animate(animation, obj, model.getObject(obj).transformation).matrix
        }

        model.tree.groups[group].forEach {
            getRecursiveMatrix(matrixCache, model, it, mat, animator, animation)
        }
    }

    private fun getSelectionVao(ctx: RenderContext, obj: IObject, selection: Nullable<ISelection>, color: IVector3): VAO? {
        val sel = selection.getOrNull() ?: return null
        return when (sel.selectionType) {
            SelectionType.OBJECT -> ctx.buffer.build(DrawMode.LINES) {
                appendObjectSelection(obj, sel, color)
            }
            SelectionType.FACE -> ctx.buffer.build(DrawMode.LINES) {
                appendFaceSelection(obj, sel, color)
            }
            SelectionType.EDGE -> ctx.buffer.build(DrawMode.LINES) {
                appendEdgeSelection(obj, sel, color)
            }
            SelectionType.VERTEX -> ctx.buffer.build(DrawMode.TRIANGLES) {
                appendVertexSelection(obj, sel, color)
            }
        }
    }

    private fun BufferPTNC.appendObjectSelection(obj: IObject, selection: ISelection, color: IVector3) {
        if (selection.isSelected(obj.ref)) {
            obj.mesh.forEachEdge { (a, b) ->
                add(a.pos, Vector2.ORIGIN, Vector3.ZERO, color)
                add(b.pos, Vector2.ORIGIN, Vector3.ZERO, color)
            }
        }
    }

    private fun BufferPTNC.appendFaceSelection(obj: IObject, selection: ISelection, color: IVector3) {

        selection.faces.filter { it.objectId == obj.id }.forEach { ref ->
            val face = obj.mesh.faces[ref.faceIndex]

            for (index in 0 until face.vertexCount) {
                val next = (index + 1) % face.vertexCount
                add(obj.mesh.pos[face.pos[index]], Vector2.ORIGIN, Vector3.ZERO, color)
                add(obj.mesh.pos[face.pos[next]], Vector2.ORIGIN, Vector3.ZERO, color)
            }
        }
    }

    private fun BufferPTNC.appendEdgeSelection(obj: IObject, selection: ISelection, color: IVector3) {

        selection.edges.filter { it.objectId == obj.id }.forEach { ref ->
            add(obj.mesh.pos[ref.firstIndex], Vector2.ORIGIN, Vector3.ZERO, color)
            add(obj.mesh.pos[ref.secondIndex], Vector2.ORIGIN, Vector3.ZERO, color)
        }
    }

    private fun BufferPTNC.appendVertexSelection(obj: IObject, selection: ISelection, color: IVector3) {

        selection.pos.filter { it.objectId == obj.id }.forEach { ref ->
            val point = obj.mesh.pos[ref.posIndex]

            MeshFactory.createCube(vec3Of(0.5), vec3Of(-0.25) + point).append(this, color)
        }
    }
}