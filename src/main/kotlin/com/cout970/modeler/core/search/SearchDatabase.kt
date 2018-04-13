package com.cout970.modeler.core.search

import com.cout970.modeler.controller.StackOverflowSnippets
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.config.KeyBind
import com.cout970.modeler.gui.rcomponents.SearchResult

/**
 * Created by cout970 on 2017/04/12.
 */

object SearchDatabase {

    private val keys get() = Config.keyBindings

    // @formatter:off
    val options = listOf(
            Entry("Set element selection mode", listOf("set", "change", "selection", "use", "element", "mode"), keys.setObjectSelectionType, "input.select.element"),
            Entry("Set quad selection mode", listOf("set", "change", "selection", "use", "quad", "mode"), keys.setFaceSelectionType, "input.select.quad"),
            Entry("Set edge selection mode", listOf("set", "change", "selection", "use", "edge", "mode"), keys.setEdgeSelectionType, "input.select.edge"),
            Entry("Set vertex selection mode", listOf("set", "change", "selection", "use", "vertex", "mode"), keys.setVertexSelectionType, "input.select.vertex"),

            Entry("Add cube", listOf("add", "new", "cube"), keys.addCube, "input.add.cube"),
            Entry("Add plane", listOf("add", "new", "plane"), keys.addPlane, "input.add.plane"),

            Entry("Undo last action", listOf("undo", "action", "fix"), keys.undo, "input.undo"),
            Entry("Redo last action", listOf("redo", "action", "fix"), keys.redo, "input.redo"),
            Entry("Copy selected part", listOf("copy", "duplicate", "mirror"), keys.copy, "input.copy"),
            Entry("Paste selected part", listOf("paste", "put", "set", "place"), keys.paste, "input.paste"),
            Entry("Cut selected part", listOf("cut", "remove", "delete"), keys.cut, "input.cut"),
            Entry("Delete selected part", listOf("delete", "remove", "clear"), keys.delete, "input.delete"),

            Entry("Set cursor to translation mode", listOf("move", "translate", "cursor"), keys.setTranslationCursorMode, "input.cursor.translation"),
            Entry("Set cursor to rotation mode", listOf("angle", "rotate", "cursor"), keys.setRotationCursorMode, "input.cursor.rotation"),
            Entry("Set cursor to scale mode", listOf("resize", "scale", "cursor"), keys.setScaleCursorMode, "input.cursor.scale"),

            Entry("New project", listOf("new", "create", "project", "model"), keys.newProject, "input.file.new"),
            Entry("Open project", listOf("open", "load", "project", "model"), keys.openProject, "input.file.open"),
            Entry("Save project", listOf("save", "store", "keep", "project", "model"), keys.saveProject, "input.file.save"),
            Entry("Save project as...", listOf("save", "store", "keep", "as", "project", "model"), keys.saveProjectAs, "input.file.save_as"),
            Entry("Import model", listOf("import", "load", "model", "project", "file"), keys.importModel, "input.file.import"),
            Entry("Export model", listOf("export", "save", "model", "project", "file"), keys.exportModel, "input.file.export"),
            Entry("Import texture", listOf("open", "load", "import", "texture", "icon", "material"), keys.importTexture, "input.texture.import"),
            Entry("Export texture", listOf("save", "create", "export", "texture", "icon", "material"), keys.exportTexture, "input.texture.export")
    )
    // @formatter:on

    fun search(field: String): List<SearchResult> {
        val result = mutableListOf<SearchResult>()
        val text = field.trim()

        options.forEach { op ->
            if (op.keywords.any { it.contains(text) || text.contains(it) }) {
                result += SearchResult(op.text, op.keyBind.toString(), op.cmd)
            }
        }
        return result.sortedBy { StackOverflowSnippets.similarity(field, it.text) }
    }

    class Entry(val text: String, val keywords: List<String>, val keyBind: KeyBind, val cmd: String)
}