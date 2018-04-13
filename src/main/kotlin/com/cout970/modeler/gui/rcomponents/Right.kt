package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.getRecursiveChildGroups
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.event.EventMaterialUpdate
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.flatMapList
import com.cout970.modeler.util.getOr
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.color.ColorConstants.transparent


data class RightPanelProps(val visible: Boolean, val modelAccessor: IModelAccessor, val state: GuiState) : RProps

class RightPanel : RStatelessComponent<RightPanelProps>() {

    override fun RBuilder.render() = div("RightPanel") {
        style {
            background { darkestColor }
            borderless()
            posY = 48f

            if (!props.visible)
                hide()
        }

        postMount {
            width = 288f
            posX = parent.width - width
            height = parent.size.y - 48f
        }

        div("Container") {

            style {
                transparent()
                borderless()
            }

            postMount {
                fillX()
                posY = 5f
                sizeY = parent.sizeY - posY
            }
            child(CreateObjectPanel::class)
            child(ModelTree::class, ModelTreeProps(props.modelAccessor))
            child(MaterialList::class, MaterialListProps(props.modelAccessor, { props.state.selectedMaterial }))
        }
    }
}

class CreateObjectPanel : RStatelessComponent<EmptyProps>() {

    override fun RBuilder.render() = div("CreateObject") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
        }

        postMount {
            marginX(5f)
            height = 64f
        }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Create Object"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }

            }

            postMount {
                marginX(50f)
                posY = 0f
                sizeY = 24f
            }
        }

        +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 28f, 32f, 32f).also {
            it.setTooltip("Create Template Cube")
        }
        +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 28f, 32f, 32f).also {
            it.setTooltip("Create Cube Mesh")
        }
        +IconButton("group.add", "addMeshCubeIcon", 75f, 28f, 32f, 32f).also {
            it.setTooltip("Create Object GroupRef")
        }
    }
}

data class ModelTreeProps(val modelAccessor: IModelAccessor) : RProps

class ModelTree : RStatelessComponent<ModelTreeProps>() {

    override fun RBuilder.render() = div("ModelTree") {

        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = 300f
            posY = 70f
        }

        postMount {
            marginX(5f)
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Model Tree"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        scrollablePanel("ModeTreeScrollPanel") {
            style {
                transparent()
                borderless()
            }

            postMount {
                posX = 5f
                posY = 24f + 5f
                sizeX = parent.sizeX - 10f
                sizeY = parent.sizeY - posY - 5f
            }

            horizontalScroll {
                style { hide() }
            }

            verticalScroll {
                style {
                    rectCorners()
                    style.minWidth = 16f
                    arrowColor = color { lightBrightColor }
                    scrollColor = color { darkColor }
                    visibleAmount = 50f
                    backgroundColor { color { lightBrightColor } }
                }
            }

            container {

                val model = props.modelAccessor.model
                val objs = model.objectMap.values
                val tree = model.groupTree
                val selected = props.modelAccessor.modelSelection.map { sel ->
                    { obj: IObjectRef -> sel.isSelected(obj) }
                }.getOr { _: IObjectRef -> false }

                var index = 0

                val allGroups = listOf(RootGroupRef) + model.getRecursiveChildGroups(RootGroupRef)
                allGroups.forEach { group ->
                    if (group != RootGroupRef) {
                        group(index++, model.getGroup(group))
                    }
                    tree.getObjects(group).forEach { ref ->
                        obj(index++, model.getObject(ref), selected(ref))
                    }
                }

                style {
                    transparent()
                    borderless()
                    height = index * 26f
                    width = 251f
                }
            }
        }
    }

    fun RBuilder.group(index: Int, group: IGroup) {
        div(group.name) {
            style {
                sizeY = 24f
                posY = index * (sizeY + 2f)
                transparent()
                borderless()
                rectCorners()

                background { lightDarkColor }
            }

            postMount {
                sizeX = parent.sizeX - 5f
            }


            +IconButton("tree.view.select.group", "group_icon", 0f, 0f, 24f, 24f).apply {
                metadata += "ref" to group.ref
            }

            +TextButton("tree.view.select.group", group.name, 24f, 0f, 172f, 24f).apply {
                transparent()
                borderless()
                fontSize = 20f
                horizontalAlign = HorizontalAlign.LEFT
                textState.padding.x = 2f
                metadata += "ref" to group.ref
            }

            if (group.visible) {
                +IconButton("tree.view.hide.group", "hideIcon", 196f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to group.ref
                    setTooltip("Hide group")
                }
            } else {
                +IconButton("tree.view.show.group", "showIcon", 196f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to group.ref
                    setTooltip("Show group")
                }
            }

            +IconButton("tree.view.delete.group", "deleteIcon", 222f, 0f, 24f, 24f).apply {
                transparent()
                borderless()
                metadata += "ref" to group.ref
                setTooltip("Delete group")
            }
        }
    }

    fun RBuilder.obj(index: Int, obj: IObject, selected: Boolean) {
        div(obj.name) {
            style {
                sizeY = 24f
                posY = index * (sizeY + 2f)
                transparent()
                borderless()
                rectCorners()

                if (selected) {
                    background { lightBrightColor }
                } else {
                    background { lightDarkColor }
                }
            }

            postMount {
                sizeX = parent.sizeX - 5f
            }

            val icon = if (obj is IObjectCube) "obj_type_cube" else "obj_type_mesh"

            +IconButton("tree.view.select.item", icon, 0f, 0f, 24f, 24f).apply {
                metadata += "ref" to obj.ref
            }
            +TextButton("tree.view.select.item", obj.name, 24f, 0f, 172f, 24f).apply {
                transparent()
                borderless()
                fontSize = 20f
                horizontalAlign = HorizontalAlign.LEFT
                textState.padding.x = 2f
                metadata += "ref" to obj.ref
            }

            +IconButton("tree.view.move.up.item", "upIcon", 144f, 0f, 24f, 24f).apply {
                transparent()
                borderless()
                metadata += "ref" to obj.ref
                setTooltip("Move object up")
            }

            +IconButton("tree.view.move.down.item", "downIcon", 170f, 0f, 24f, 24f).apply {
                transparent()
                borderless()
                metadata += "ref" to obj.ref
                setTooltip("Move object down")
            }

            if (obj.visible) {
                +IconButton("tree.view.hide.item", "hideIcon", 196f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    setTooltip("Hide object")
                }
            } else {
                +IconButton("tree.view.show.item", "showIcon", 196f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    setTooltip("Show object")
                }
            }

            +IconButton("tree.view.delete.item", "deleteIcon", 222f, 0f, 24f, 24f).apply {
                transparent()
                borderless()
                metadata += "ref" to obj.ref
                setTooltip("Delete object")
            }
        }
    }
}


data class MaterialListProps(val modelAccessor: IModelAccessor, val selectedMaterial: () -> IMaterialRef) : RProps

class MaterialList : RStatelessComponent<MaterialListProps>() {

    override fun RBuilder.render() = div("MaterialList") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = 300f
            posY = 375f
        }

        postMount {
            marginX(5f)
        }

        on<EventModelUpdate> { rerender() }
        on<EventMaterialUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Material List"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        div("Buttons") {
            style {
                transparent()
                borderless()
                posY = 24f
                sizeY = 32f
            }

            postMount {
                marginX(5f)
            }

            +IconButton("material.view.import", "add_material", 0f, 0f, 32f, 32f).also {
                it.setTooltip("Import material")
            }

            +IconButton("material.view.duplicate", "duplicate_material", 32f, 0f, 32f, 32f).also {
                it.setTooltip("Duplicate material")
                it.metadata += "ref" to props.selectedMaterial()

            }

            +IconButton("material.view.load", "load_material", 64f, 0f, 32f, 32f).also {
                it.setTooltip("Load different texture")
                it.metadata += "ref" to props.selectedMaterial()
            }

            +IconButton("material.view.remove", "remove_material", 96f, 0f, 32f, 32f).also {
                it.setTooltip("Delete material")
                it.metadata += "ref" to props.selectedMaterial()
            }

            +IconButton("material.view.inverse_select", "inverse_select_material", 128f, 0f, 32f, 32f).also {
                it.setTooltip("Select objects with this material")
                it.metadata += "ref" to props.selectedMaterial()
            }
        }

        scrollablePanel("MaterialListScrollPanel") {
            style {
                transparent()
                borderless()
            }

            postMount {
                posX = 0f
                posY = 24f + 32f + 5f
                sizeX = parent.sizeX - 5f
                sizeY = parent.sizeY - posY - 5f
            }

            horizontalScroll {
                style { hide() }
            }

            verticalScroll {
                style {
                    rectCorners()
                    style.minWidth = 16f
                    arrowColor = color { lightBrightColor }
                    scrollColor = color { darkColor }
                    visibleAmount = 50f
                    backgroundColor { color { lightBrightColor } }
                }
            }

            container {

                val model = props.modelAccessor.model
                val selection = props.modelAccessor.modelSelection
                val materialRefs = (model.materialRefs + listOf(MaterialRefNone))
                val selectedMaterial = props.selectedMaterial()

                val materialOfSelectedObjects = selection
                        .map { it to it.objects }
                        .map { (sel, objs) -> objs.filter(sel::isSelected) }
                        .map { it.map { model.getObject(it).material } }
                        .flatMapList()


                style {
                    transparent()
                    borderless()
                    sizeX = 256f
                    sizeY = materialRefs.size * (24f + 2f)
                }

                materialRefs.forEachIndexed { index, ref ->
                    val material = model.getMaterial(ref)

                    val color = if (ref == selectedMaterial) {
                        Config.colorPalette.brightColor.toColor()
                    } else {
                        Config.colorPalette.lightDarkColor.toColor()
                    }

                    div(material.name) {
                        style {
                            sizeY = 24f
                            posY = index * (sizeY + 2f)
                            transparent()
                            borderless()
                            rectCorners()
                            backgroundColor { color }
                        }

                        postMount {
                            marginX(5f)
                        }

                        val icon = if (ref in materialOfSelectedObjects) "material_in_use" else ""

                        +IconButton("material.view.select", icon, 0f, 0f, 24f, 24f).apply {
                            metadata += "ref" to material.ref
                        }

                        +TextButton("material.view.select", material.name, 24f, 0f, 196f, 24f).apply {
                            horizontalAlign = HorizontalAlign.LEFT
                            textState.padding.x = 2f
                            fontSize = 20f
                            transparent()
                            metadata += "ref" to material.ref
                        }

                        +IconButton("material.view.apply", "apply_material", 222f, 0f, 24f, 24f).apply {
                            transparent()
                            borderless()
                            setTooltip("Apply material")
                            metadata += "ref" to material.ref
                        }
                    }
                }
            }
        }
    }
}