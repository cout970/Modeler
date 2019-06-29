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
            Entry("Set element selection mode", listOf("set", "change", "selection", "use", "element", "mode"), keys.setObjectSelectionType, "set.selection.type.object"),
            Entry("Set quad selection mode", listOf("set", "change", "selection", "use", "quad", "mode"), keys.setFaceSelectionType, "set.selection.type.face"),
            Entry("Set edge selection mode", listOf("set", "change", "selection", "use", "edge", "mode"), keys.setEdgeSelectionType, "set.selection.type.edge"),
            Entry("Set vertex selection mode", listOf("set", "change", "selection", "use", "vertex", "mode"), keys.setVertexSelectionType, "set.selection.type.vertex"),

            Entry("Add cube", listOf("add", "new", "cube"), keys.addCube, "input.add.cube"),
            Entry("Add plane", listOf("add", "new", "plane"), keys.addPlane, "input.add.plane"),
            Entry("Toggle visibility", listOf("show", "hide", "view"), keys.toggleVisibility, "model.toggle.visibility"),

            Entry("Undo last action", listOf("undo", "action", "fix"), keys.undo, "model.undo"),
            Entry("Redo last action", listOf("redo", "action", "fix"), keys.redo, "model.redo"),
            Entry("Copy selected part", listOf("copy", "duplicate", "mirror"), keys.copy, "model.selection.copy"),
            Entry("Paste selected part", listOf("paste", "put", "set", "place"), keys.paste, "model.selection.paste"),
            Entry("Cut selected part", listOf("cut", "remove", "delete"), keys.cut, "model.selection.cut"),
            Entry("Delete selected part", listOf("delete", "remove", "clear"), keys.delete, "model.selection.delete"),

            Entry("Set cursor to translation mode", listOf("move", "translate", "cursor"), keys.setTranslationCursorMode, "cursor.set.mode.translate"),
            Entry("Set cursor to rotation mode", listOf("angle", "rotate", "cursor"), keys.setRotationCursorMode, "cursor.set.mode.rotate"),
            Entry("Set cursor to scale mode", listOf("resize", "scale", "cursor"), keys.setScaleCursorMode, "cursor.set.mode.scale"),

            Entry("Show/Hide left panel", listOf("show", "hide", "toggle", "panel", "left"), keys.showLeftPanel, "show.left.panel"),
            Entry("Show/Hide right panel", listOf("show", "hide", "toggle", "panel", "right"), keys.showRightPanel, "show.right.panel"),
            Entry("Show/Hide bottom panel", listOf("show", "hide", "toggle", "panel", "bottom"), keys.showBottomPanel, "show.bottom.panel"),

            Entry("Toggle Ortho Projection", listOf("show", "hide", "toggle", "otho", "projection", "view"), keys.switchOrthoProjection, "view.switch.ortho"),
            Entry("Set view to texture mode", listOf("set", "texture", "mode", "view"), keys.setTextureMode, "view.set.texture.mode"),
            Entry("Set view to model mode", listOf("set", "model", "mode", "view"), keys.setModelMode, "view.set.model.mode"),

            Entry("Select all", listOf("select", "all", "everything"), keys.selectAll, "view.set.model.mode"),
            Entry("Move camera to cursor", listOf("camera", "cursor", "move"), keys.moveCameraToCursor, "camera.move.to.cursor"),

            Entry("Scale texture up", listOf("scale", "texture", "up"), keys.scaleTextureUp, "model.texture.scale.up"),
            Entry("Scale texture down", listOf("scale", "texture", "down"), keys.scaleTextureDown, "model.texture.scale.down"),

            Entry("Join selected objects", listOf("join", "merge", "object"), keys.joinObjects, "model.obj.join"),
            Entry("Auto scale uv", listOf("uv", "scale", "auto", "arrange"), keys.arrangeUvs, "model.obj.arrange.uv"),
            Entry("Extrude faces", listOf("face", "extrude"), keys.extrudeFace, "model.face.extrude"),
            Entry("Set isometric view", listOf("camera", "isometric", "view"), keys.setIsometricView, "camera.set.isometric"),

            Entry("Add animation", listOf("new", "create", "add", "animation"), keys.addAnimation, "animation.add"),
            Entry("Create colored material", listOf("new", "create", "color", "material"), keys.newColoredMaterial, "material.new.colored"),

//            Entry("layoutChangeMode", listOf(""), keys.layoutChangeMode, ""),
//            Entry("moveLayoutSplitterLeft", listOf(""), keys.moveLayoutSplitterLeft, ""),
//            Entry("moveLayoutSplitterRight", listOf(""), keys.moveLayoutSplitterRight, ""),
//            Entry("moveLayoutSplitterUp", listOf(""), keys.moveLayoutSplitterUp, ""),
//            Entry("moveLayoutSplitterDown", listOf(""), keys.moveLayoutSplitterDown, ""),
//            Entry("newCanvas", listOf(""), keys.newCanvas, ""),
//            Entry("deleteCanvas", listOf(""), keys.deleteCanvas, ""),

            Entry("New project", listOf("new", "create", "project", "model"), keys.newProject, "project.new"),
            Entry("Load project", listOf("open", "load", "project", "model"), keys.loadProject, "project.load"),
            Entry("Save project", listOf("save", "store", "keep", "project", "model"), keys.saveProject, "project.save"),
            Entry("Save project as...", listOf("save", "store", "keep", "as", "project", "model"), keys.saveProjectAs, "project.save.as"),
            Entry("Import model", listOf("import", "load", "model", "project", "file"), keys.importModel, "model.import"),
            Entry("Export model", listOf("export", "save", "model", "project", "file"), keys.exportModel, "model.export")
//            Entry("Import texture", listOf("open", "load", "import", "texture", "icon", "material"), keys.importTexture, "input.texture.import"),
//            Entry("Export texture", listOf("save", "create", "export", "texture", "icon", "material"), keys.exportTexture, "input.texture.export"),
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