package com.cout970.modeler.gui.views

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.leguicomp.forEachRecursive
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.rcomponents.RootComp
import com.cout970.modeler.gui.rcomponents.RootProps
import com.cout970.modeler.util.toJoml2f
import com.cout970.reactive.core.RContext
import com.cout970.reactive.core.Renderer
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.transparent
import com.cout970.reactive.nodes.child
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.system.layout.LayoutManager

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorView : IView {

    override lateinit var gui: Gui
    var ctx: RContext? = null


    override val base = panel {
        borderless()
        transparent()
    }

    fun init() {
        ctx = Renderer.render(base) {
            child(RootComp::class, RootProps(gui))
        }

        ctx!!.registerUpdateListener { (mount, _) ->
            update(mount)
        }
        update(base)
    }

    fun update(mount: Component) {
        gui.root.bindButtons(gui.buttonBinder)
        gui.root.bindProperties(gui.state)
        gui.root.loadResources(gui.resources)
        LayoutManager.getInstance().layout(gui.root)
        mount.forEachRecursive {
            it.metadata["Dispatcher"] = gui.dispatcher
        }
    }

    override fun reBuild(newSize: IVector2) {

        base.position.set(0f, 0f)
        base.size = newSize.toJoml2f()

        if (ctx == null) {
            init()
        } else {
            Renderer.rerender(ctx!!)
        }
    }
}

data class VisibleElements(val left: Boolean, val bottom: Boolean, val right: Boolean)