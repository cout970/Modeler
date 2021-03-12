package com.cout970.reactive.nodes

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.dsl.childrenAsNodes
import com.cout970.reactive.dsl.postMount
import com.cout970.reactive.dsl.replaceListener
import org.joml.Vector2f
import org.liquidengine.legui.component.SelectBox
import org.liquidengine.legui.component.misc.listener.selectbox.SelectBoxClickListener
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.system.layout.LayoutManager

fun RBuilder.selectBox(key: String? = null, block: ComponentBuilder<SelectBox<String>>.() -> Unit = {}) {
    +ComponentBuilder(SelectBox<String>()).apply(block).also {

        postMount {
            val selectBox = it.component
            val mouseClickEventListener = object : SelectBoxClickListener<String>(selectBox) {

                override fun process(event: MouseClickEvent<*>) {
                    if (event.action == MouseClickEvent.MouseClickAction.CLICK) {
                        val frame = event.frame
                        val selectBoxLayer = selectBox.selectBoxLayer
                        val collapsed = selectBox.isCollapsed
                        selectBox.isCollapsed = !collapsed
                        if (collapsed) {
                            val layerSize = Vector2f(frame.container.size)
                            selectBoxLayer.size = layerSize

                            frame.addLayer(selectBoxLayer)
                            LayoutManager.getInstance().layout(frame)
                        } else {
                            frame.removeLayer(selectBoxLayer)
                        }
                    }
                }

            }
            selectBox.selectionButton.listenerMap.replaceListener(MouseClickEvent::class.java, mouseClickEventListener)
            selectBox.expandButton.listenerMap.replaceListener(MouseClickEvent::class.java, mouseClickEventListener)
            selectBox.selectBoxLayer.listenerMap.replaceListener(MouseClickEvent::class.java, mouseClickEventListener)
        }

        it.childrenAsNodes()
    }.build(key)
}

