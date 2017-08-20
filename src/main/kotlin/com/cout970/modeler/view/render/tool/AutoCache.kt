package com.cout970.modeler.view.render.tool

import com.cout970.glutilities.tessellator.VAO

/**
 * Created by cout970 on 2017/07/24.
 */
class AutoCache(vararg val flags: CacheFrags) {

    var vao: VAO? = null
    var storedHash: Int = -1

    fun getOrCreate(ctx: RenderContext, create: () -> VAO): VAO {
        val hash = getHash(ctx)
        if (vao == null || hash != storedHash) {
            storedHash = hash
            vao?.close()
            vao = create()
        }
        return vao!!
    }

    fun getHash(ctx: RenderContext): Int {
        var hash = -1
        if (CacheFrags.MODEL in flags) {
            hash = (hash shl 1) xor ctx.gui.state.modelHash
        }
        if (CacheFrags.SELECTION_MODEL in flags) {
            hash = (hash shl 1) xor ctx.gui.state.modelSelectionHash
        }
        if (CacheFrags.SELECTION_TEXTURE in flags) {
            hash = (hash shl 1) xor ctx.gui.state.textureSelectionHash
        }
        if (CacheFrags.MATERIAL in flags) {
            hash = (hash shl 1) xor ctx.gui.state.materialsHash
        }
        if (CacheFrags.VISIBILITY in flags) {
            hash = (hash shl 1) xor ctx.gui.state.visibilityHash
        }
        if (CacheFrags.CURSOR in flags) {
            hash = (hash shl 1) xor ctx.gui.canvasManager.cursor.hashCode()
        }
        return hash
    }

    fun reset() {
        vao = null
    }

    fun get(): VAO? = vao

    fun set(new: VAO) {
        vao = new
    }
}

enum class CacheFrags {
    MODEL,
    SELECTION_MODEL,
    SELECTION_TEXTURE,
    MATERIAL,
    VISIBILITY,
    CURSOR
}