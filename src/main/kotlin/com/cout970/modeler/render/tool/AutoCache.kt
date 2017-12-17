package com.cout970.modeler.render.tool

import com.cout970.glutilities.tessellator.VAO

/**
 * Created by cout970 on 2017/07/24.
 */
class AutoCache(vararg val flags: CacheFlags) {

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
        if (CacheFlags.MODEL in flags) {
            hash = (hash shl 1) xor ctx.gui.state.modelHash
        }
        if (CacheFlags.SELECTION_MODEL in flags) {
            hash = (hash shl 1) xor ctx.gui.state.modelSelectionHash
        }
        if (CacheFlags.SELECTION_TEXTURE in flags) {
            hash = (hash shl 1) xor ctx.gui.state.textureSelectionHash
        }
        if (CacheFlags.MATERIAL in flags) {
            hash = (hash shl 1) xor ctx.gui.state.materialsHash
        }
        if (CacheFlags.VISIBILITY in flags) {
            hash = (hash shl 1) xor ctx.gui.state.visibilityHash
        }
        if (CacheFlags.MODEL_CURSOR in flags) {
            hash = (hash shl 1) xor (ctx.gui.cursorManager.modelCursor?.hashCode() ?: -1)
        }
        if (CacheFlags.TEXTURE_CURSOR in flags) {
            hash = (hash shl 1) xor (ctx.gui.cursorManager.textureCursor?.hashCode() ?: -1)
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

enum class CacheFlags {
    MODEL,
    SELECTION_MODEL,
    SELECTION_TEXTURE,
    MATERIAL,
    VISIBILITY,
    MODEL_CURSOR,
    TEXTURE_CURSOR,
}