package com.cout970.modeler.gui.rcomponents.right

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.flatMapList
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.scrollablePanel
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign

data class MaterialListProps(val programState: IProgramState, val selectedMaterial: () -> IMaterialRef) : RProps

class MaterialList : RStatelessComponent<MaterialListProps>() {

    override fun RBuilder.render() = div("MaterialList") {
        style {
            height = 300f
            posY = 375f
            classes("left_panel_material_list")
        }

        postMount {
            marginX(5f)
            posY = parent.height / 2f
            height = parent.height / 2f
        }

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
        onCmd("updateMaterial") { rerender() }

        comp(FixedLabel("Material List")) {
            style {
                classes("fixed_label", "material_list_label")
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
                floatLeft(0f, 0f)
            }

            +IconButton("material.view.import", "add_material", 0f, 0f, 32f, 32f).also {
                it.setTooltip("Import material")
            }

            +IconButton("material.new.colored", "add_color_material", 0f, 0f, 32f, 32f).also {
                it.setTooltip("Create color material")
            }

            +IconButton("material.view.duplicate", "duplicate_material", 0f, 0f, 32f, 32f).also {
                it.setTooltip("Duplicate material")
                it.metadata += "ref" to props.selectedMaterial()
            }

            +IconButton("material.view.load", "load_material", 0f, 0f, 32f, 32f).also {
                it.setTooltip("Load different texture")
                it.metadata += "ref" to props.selectedMaterial()
            }

            +IconButton("material.view.remove", "remove_material", 0f, 0f, 32f, 32f).also {
                it.setTooltip("Delete material")
                it.metadata += "ref" to props.selectedMaterial()
            }

            +IconButton("material.view.inverse_select", "picker", 0f, 0f, 32f, 32f).also {
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
                posX = 5f
                posY = 24f + 32f + 5f
                sizeX = parent.sizeX - 5f
                sizeY = parent.sizeY - posY - 5f
            }

            horizontalScroll {
                style { hide() }
            }

            verticalScroll {
                style {
                    style.setMinWidth(16f)
                    visibleAmount = 50f
                    style.setTop(0f)
                    style.setBottom(0f)
                    classes("left_panel_material_list_scroll")
                }
            }

            viewport {
                style {
                    style.setRight(18f)
                    style.setBottom(0f)
                    classes("left_panel_material_list_box")
                }
            }

            container {

                val model = props.programState.model
                val selection = props.programState.modelSelection
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
                    sizeY = materialRefs.size * (24f + 2f) + 10f
                }

                materialRefs.forEachIndexed { index, ref ->
                    val material = model.getMaterial(ref)

                    div(material.name) {
                        style {
                            sizeY = 24f
                            posY = 5f + index * (sizeY + 2f)
                            classes("material_list_item")

                            if (ref == selectedMaterial) {
                                classes("material_list_item_selected")
                            }
                        }

                        postMount {
                            marginX(5f)
                        }

                        val icon = if (ref in materialOfSelectedObjects) "material_in_use" else "material"

                        +IconButton("material.view.select", icon, 0f, 0f, 24f, 24f).apply {
                            metadata += "ref" to material.ref
                        }

                        +TextButton("material.view.select", material.name, 24f, 0f, 196f - 24f, 24f).apply {
                            horizontalAlign = HorizontalAlign.LEFT
                            paddingLeft(2f)
                            fontSize = 20f
                            transparent()
                            metadata += "ref" to material.ref
                        }

                        +IconButton("material.view.apply", "apply_material", 198f, 0f, 24f, 24f).apply {
                            transparent()
                            borderless()
                            setTooltip("Apply material")
                            metadata += "ref" to material.ref
                        }

                        +IconButton("material.view.config", "config_material", 222f, 0f, 24f, 24f).apply {
                            transparent()
                            borderless()
                            setTooltip("Configure")
                            metadata += "material" to material
                        }
                    }
                }
            }
        }
    }
}