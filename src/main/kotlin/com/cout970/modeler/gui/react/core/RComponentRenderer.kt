package com.cout970.modeler.gui.react.core

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.util.toIVector
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/09/23.
 */
object RComponentRenderer {

    fun render(root: Container<Component>, gui: Gui, virtualTree: () -> Component) {
        buildAll(RContext(root, gui, virtualTree))
    }

    fun buildAll(ctx: RContext) {

        val buildContext = RBuildContext(ctx.root.size.toIVector(), ctx.gui.root.context)
        val root = expandSubTree(ctx.virtualTree(), buildContext, ctx)

        ctx.root.also {
            it.clearChilds()
            it.add(root)
        }
        ctx.gui.let { gui ->
            gui.root.bindButtons(gui.buttonBinder)
            gui.root.bindProperties(gui.state)
            gui.root.loadResources(gui.resources)
        }
    }

    fun buildComponent(wrapper: RComponentWrapper<*, *, *>) {
        val buildCtx = RBuildContext(
                parentSize = wrapper.getParent()?.size?.toIVector() ?: wrapper.getSize().toIVector(),
                leguiCtx = wrapper.component.context.gui.root.context
        )
        updateSubTree(wrapper, buildCtx, wrapper.component.context)
        wrapper.component.context.gui.let { gui ->
            gui.root.bindButtons(gui.buttonBinder)
            gui.root.bindProperties(gui.state)
            gui.root.loadResources(gui.resources)
        }
    }

    private fun updateSubTree(wrapper: RComponentWrapper<*, *, *>, buildCtx: RBuildContext, ctx: RContext) {
        val oldTree = wrapper.getChilds().firstOrNull()

        val newTree = wrapper.buildSubTree(buildCtx)
        val expandedTree = expandSubTree(newTree, buildCtx, ctx)

        val finalTree = if (oldTree != null) mergeTrees(oldTree, expandedTree) else expandedTree

        wrapper.clearChilds()
        wrapper.add(finalTree)
        wrapper.onUpdateChild()
    }

    @Suppress("UNCHECKED_CAST")
    private fun expandSubTree(tree: Component, buildCtx: RBuildContext, ctx: RContext): Component {
        if (tree is RComponentWrapper<*, *, *>) return tree.apply { this.init(ctx); updateSubTree(this, buildCtx, ctx) }
        if (tree !is Container<*>) return tree
        val container = tree as Container<Component>

        val childs = container.childs.map { expandSubTree(it, updateBuildContext(buildCtx, container), ctx) }

        return container.apply { clearChilds(); addAll(childs) }
    }

    private fun updateBuildContext(old: RBuildContext, parent: Component): RBuildContext {
        return old.copy(parentSize = parent.size.toIVector())
    }

    @Suppress("UNCHECKED_CAST")
    private fun mergeTrees(old: Component, new: Component): Component {
        if (old.javaClass != new.javaClass) return new
        if (old is RComponentWrapper<*, *, *> && new is RComponentWrapper<*, *, *>) {
            val oldC = old.component as RComponent<Any, Any>
            val newC = new.component as RComponent<Any, Any>

            oldC.componentWillUnmount()
            newC.componentWillMount()
            newC.transferState(oldC.state)
            newC.transferProps(oldC.props)
            newC.componentDidMount()
            return new
        }
        if (old is Container<*> && new is Container<*>) {
            if (old.size != new.size) {
                // I don't know how to handle this situation, so I will use the Ostrich algorithm,
                // hide my head in the ground until the problem goes away
            } else {
                val childs = old.childs.zip(new.childs).map { (oldC, newC) ->
                    mergeTrees(oldC, newC)
                }
                return (new as Container<Component>).apply { clearChilds(); addAll(childs) }
            }
        }
        return new
    }
}