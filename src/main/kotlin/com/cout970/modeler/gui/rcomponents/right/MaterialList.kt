package com.cout970.modeler.gui.rcomponents.right

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventMaterialUpdate
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.flatMapList
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.scrollablePanel
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign

data class MaterialListProps(val modelAccessor: IModelAccessor, val selectedMaterial: () -> IMaterialRef) : RProps

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
                    style.minWidth = 16f
                    arrowColor = color { bright1 }
                    visibleAmount = 50f
                    style.top = 0f
                    style.bottom = 0f
                    classes("left_panel_material_list_scroll")
                }
            }

            viewport {
                style {
                    style.right = 18f
                    style.bottom = 0f
                    classes("left_panel_material_list_box")
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