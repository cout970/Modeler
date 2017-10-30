package com.cout970.modeler.gui.react.core

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.util.toIVector
import org.liquidengine.legui.component.Component


/**
 * Created by cout970 on 2017/09/23.
 */
object RComponentRenderer {

    fun render(root: Component, gui: Gui, virtualTree: () -> Component) {
        buildAll(RContext(root, gui, virtualTree))
    }

    fun buildAll(ctx: RContext) {

        val buildContext = RBuildContext(ctx.root.size.toIVector(), ctx.gui.root.context)
        val root = expandSubTree(null, ctx.virtualTree(), buildContext, ctx)

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

        buildComponentHelper(wrapper, buildCtx, wrapper.component.context)

        wrapper.component.context.gui.let { gui ->
            gui.root.bindButtons(gui.buttonBinder)
            gui.root.bindProperties(gui.state)
            gui.root.loadResources(gui.resources)
        }
    }

    private fun buildComponentHelper(wrapper: RComponentWrapper<*, *, *>, buildCtx: RBuildContext, ctx: RContext) {
        val oldTree = wrapper.getChilds().firstOrNull()
        val newTree = wrapper.buildSubTree(buildCtx)

        val expandedTree = expandSubTree(oldTree, newTree, buildCtx, ctx)

        val finalTree = if (oldTree != null) mergeTrees(oldTree, expandedTree) else expandedTree

        wrapper.clearChilds()
        wrapper.add(finalTree)
        wrapper.onUpdateChild()
    }

    @Suppress("UNCHECKED_CAST")
    private fun expandSubTree(old: Component?, new: Component, buildCtx: RBuildContext, ctx: RContext): Component {

        // generates next level nad moves props and state if needed
        if (new is RComponentWrapper<*, *, *>) {

            if (!new.initialized) {
                new.init(ctx)
            }

            if (old is RComponentWrapper<*, *, *> && old.initialized) {
                val oldC = old.component as RComponent<Any, Any>
                val newC = new.component as RComponent<Any, Any>

                oldC.componentWillUnmount()
                newC.componentWillMount()
                newC.transferState(oldC.state)
                newC.componentDidMount()
            }

            val oldTree = if (old is Component) old.childs?.firstOrNull() else null
            val newTree = new.buildSubTree(buildCtx)

            val expandedTree = expandSubTree(oldTree, newTree, buildCtx, ctx)

            val finalTree = if (oldTree != null) mergeTrees(oldTree, expandedTree) else expandedTree

            new.clearChilds()
            new.add(finalTree)
            new.onUpdateChild()
            return new
        }

        if (new !is Component) return new
        val newContainer = new as Component

        if (old is Component) {
            val oldContainer = old as Component

            if (oldContainer.count() == newContainer.count()) {
                val childs = newContainer.childs.zip(oldContainer.childs).map { (newChild, oldChild) ->
                    expandSubTree(oldChild, newChild, updateBuildContext(buildCtx, newContainer), ctx)
                }

                return newContainer.apply { clearChilds(); addAll(childs) }
            }
        }
        val childs = newContainer.childs.map {
            expandSubTree(null, it, updateBuildContext(buildCtx, newContainer), ctx)
        }

        return newContainer.apply { clearChilds(); addAll(childs) }
    }

    private fun updateBuildContext(old: RBuildContext, parent: Component): RBuildContext =
            old.copy(parentSize = parent.size.toIVector())

    @Suppress("UNCHECKED_CAST")
    private fun mergeTrees(old: Component, new: Component): Component {
        if (old.javaClass != new.javaClass) return new
        if (new is RComponentWrapper<*, *, *>) {
            return new
        }
        if (old is Component && new is Component) {
            return if (old.count() != new.count()) {
                // I don't know how to handle this situation, so I will use the Ostrich algorithm,
                // hide my head in the ground until the problem goes away
                new
            } else {
                val childs = old.childs.zip(new.childs).map { (oldC, newC) ->
                    mergeTrees(oldC, newC)
                }
                (new as Component).apply { clearChilds(); addAll(childs) }
            }
        }
        return new
    }
}