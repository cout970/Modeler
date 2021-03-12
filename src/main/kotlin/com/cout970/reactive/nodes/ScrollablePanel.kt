package com.cout970.reactive.nodes

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RDescriptor
import com.cout970.reactive.core.RNode
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.ScrollablePanel


class ScrollablePanelDescriptor : RDescriptor {
    override fun mapToComponent(): Component = ScrollablePanel()
}

class ScrollablePanelBuilder : RBuilder() {

    private var vertical: ComponentBuilder<ScrollBar>.() -> Unit = {}
    private var horizontal: ComponentBuilder<ScrollBar>.() -> Unit = {}
    private var container: ComponentBuilder<Component>.() -> Unit = {}
    private var viewport: ComponentBuilder<Component>.() -> Unit = {}

    fun container(func: ComponentBuilder<Component>.() -> Unit) {
        container = func
    }

    fun viewport(func: ComponentBuilder<Component>.() -> Unit) {
        viewport = func
    }

    fun verticalScroll(func: ComponentBuilder<ScrollBar>.() -> Unit) {
        vertical = func
    }

    fun horizontalScroll(func: ComponentBuilder<ScrollBar>.() -> Unit) {
        horizontal = func
    }

    override fun toDescriptor(): RDescriptor = ScrollablePanelDescriptor()

    override fun build(key: String?): RNode {
        val panel = ScrollablePanel()

        return ComponentBuilder(panel).apply {
            deferred = this@ScrollablePanelBuilder.deferred
            listeners.addAll(this@ScrollablePanelBuilder.listeners)

            comp(panel.verticalScrollBar, "VerticalScroll") {
                vertical()
            }

            comp(panel.horizontalScrollBar, "HorizontalScroll") {
                horizontal()
            }

            comp(panel.viewport, "Viewport") {
                viewport()
                comp(panel.container, "Container") {
                    container()
                }
            }

            children.forEach { +it }
        }.build(key)
    }
}

fun RBuilder.scrollablePanel(key: String? = null, block: ScrollablePanelBuilder.() -> Unit = {}) =
    +ScrollablePanelBuilder().apply(block).build(key)

fun ScrollablePanelBuilder.style(func: ScrollablePanel.() -> Unit) {
    deferred = { (it as ScrollablePanel).func() }
}


