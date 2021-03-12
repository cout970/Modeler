package com.cout970.reactive.core

import org.liquidengine.legui.component.Component

object Renderer {

    const val METADATA_KEY = "key"
    const val METADATA_COMPONENTS = "ReactiveRComponents"
    const val METADATA_NODE_TREE = "ReactiveNodeTree"
    const val METADATA_POST_MOUNT = "ReactivePostMount"

    // This lock can be used to avoid critical races between threads
    val updateLock = Any()

    fun render(mountPoint: Component, func: RBuilder.() -> Unit): RContext {
        return render(mountPoint, buildNode(func))
    }

    fun render(mountPoint: Component, app: RNode): RContext {
        val ctx = RContext(mountPoint, app)
        updateSubTree(ctx, mountPoint, app)
        return ctx
    }

    fun rerender(ctx: RContext) {
        updateSubTree(ctx, ctx.mountPoint, ctx.app)
    }

    internal fun <S, P> scheduleUpdate(comp: RComponent<P, S>, updateFunc: S.() -> S, setter: (S) -> Unit)
            where S : RState, P : RProps {

        if (!comp.mounted) {
            throw IllegalStateException("Trying to update a unmounted component!")
        }
        AsyncManager.runLater {
            val newState = updateFunc(comp.state)
            if (comp.shouldComponentUpdate(comp.props, newState)) {
                setter(newState)
                val ctx = comp.ctx
                val mount = comp.mountPoint
                comp.componentWillUpdate()
                updateSubTree(ctx, mount, mount.metadata[METADATA_NODE_TREE] as RNode)
            } else {
                setter(newState)
            }
        }
    }

    private fun updateSubTree(ctx: RContext, mount: Component, node: RNode) {
        synchronized(updateLock) {
            preUpdate(ctx)
            unmountAllRComponents(ctx, mount)
            ReconciliationManager.traverse(ctx, mount, node)
            postUpdate(ctx)
            callPostMount(mount)
            ctx.updateListeners.forEach { it(mount to node) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun callPostMount(comp: Component) {
        comp.metadata[Renderer.METADATA_POST_MOUNT]?.let { func ->
            (func as? ((Component) -> Unit))?.invoke(comp)
        }
        comp.childComponents.forEach {
            callPostMount(it)
        }
    }

    private fun preUpdate(ctx: RContext) {
        ctx.mountedComponents.clear()
        ctx.unmountedComponents.clear()
    }

    private fun postUpdate(ctx: RContext) {
        ctx.mountedComponents.filter { it !in ctx.unmountedComponents }.forEach {
            it.componentWillMount()
            it.componentDidMount()
        }
        ctx.unmountedComponents.filter { it !in ctx.mountedComponents }.forEach {
            it.componentWillUnmount()
        }
    }

    private fun unmountAllRComponents(ctx: RContext, comp: Component) {
        comp.unmountComponents(ctx)
        comp.childComponents.forEach { unmountAllRComponents(ctx, it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Component.unmountComponents(ctx: RContext) {
        if (METADATA_COMPONENTS in metadata) {
            val list = metadata[METADATA_COMPONENTS] as MutableList<RComponent<*, *>>

            list.filter { it.mounted }.forEach {
                ctx.unmountedComponents.add(it); it.mounted = false
            }
        }
    }
}