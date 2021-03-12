package com.cout970.reactive.core

import com.cout970.reactive.nodes.RComponentDescriptor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.ScrollablePanel
import org.liquidengine.legui.event.Event

object ReconciliationManager {

    private val strategies = mutableMapOf<Class<*>, IMergeStrategy>(
        ScrollBar::class.java to ScrollBarMergeStrategy,
        ScrollablePanel::class.java to ScrollablePanelMergeStrategy
    )

    fun registerMergeStrategy(componentClass: Class<*>, strategy: IMergeStrategy) {
        strategies[componentClass] = strategy
    }

    internal fun traverse(ctx: RContext, comp: Component, childNodes: RNode) {

        val children: List<Pair<Component, List<RNode>>> = expandLayer(ctx, comp, childNodes)
        val diffResult = diff(comp.childComponents, children)
        val newChilds = mutableListOf<Component>()

        diffResult.forEach { res ->
            when (res) {
                is DiffResult.ResultNew -> {
                    traverse(ctx, res.new, res.nodes.toFragment())
                    newChilds.add(res.new)
                }
                is DiffResult.ResultChanged -> {
                    traverse(ctx, res.new, res.nodes.toFragment())
                    newChilds.add(res.new)
                }
                is DiffResult.ResultSame -> {
                    newChilds.add(merge(ctx, res.old, res.new, res.nodes))
                }
                is DiffResult.ResultRemoved -> {
                    // NO-OP
                }
            }
        }

        comp.clearChildComponents()
        comp.addAll(newChilds)
        comp.metadata[Renderer.METADATA_NODE_TREE] = childNodes
    }

    sealed class DiffResult {

        class ResultNew(val new: Component, val nodes: List<RNode>) : DiffResult()
        class ResultRemoved(val old: Component) : DiffResult()
        class ResultSame(val old: Component, val new: Component, val nodes: List<RNode>) : DiffResult()
        class ResultChanged(val old: Component, val new: Component, val nodes: List<RNode>) : DiffResult()
    }

    class IndexedComponent(val key: String, val comp: Component, val nodes: List<RNode>)

    private fun getKey(comp: Component, index: Int) =
        comp.metadata[Renderer.METADATA_KEY]?.toString() ?: index.toString()

    private fun diff(oldChildren: List<Component>, newChildren: List<Pair<Component, List<RNode>>>): List<DiffResult> {

        val oldSet = oldChildren.mapIndexed { index, component ->
            IndexedComponent(getKey(component, index), component, emptyList())
        }
        val newSet = newChildren.mapIndexed { index, (component, nodes) ->
            IndexedComponent(getKey(component, index), component, nodes)
        }

        val result = mutableListOf<DiffResult>()
        var index = 0

        newSet.forEach { newPair ->
            if (index >= oldSet.size) {

                result += DiffResult.ResultNew(
                    new = newPair.comp,
                    nodes = newPair.nodes
                )

            } else {
                var oldPair = oldSet[index]

                if (newPair.key == oldPair.key) {
                    val diffResult = if (newPair.comp.javaClass == oldPair.comp.javaClass) {
                        DiffResult.ResultSame(
                            old = oldPair.comp,
                            new = newPair.comp,
                            nodes = newPair.nodes
                        )
                    } else {
                        DiffResult.ResultChanged(
                            old = oldPair.comp,
                            new = newPair.comp,
                            nodes = newPair.nodes
                        )
                    }
                    result += diffResult
                    index++

                } else {
                    var foundIndex = -1

                    // find old component with the same key, so any element between here and the old comp, are removed elements
                    for (newIndex in (index + 1) until oldSet.size) {
                        oldPair = oldSet[newIndex]
                        if (newPair.key == oldPair.key) {
                            foundIndex = newIndex
                            break
                        }
                    }

                    if (foundIndex != -1) {
                        // index to foundIndex were removed

                        (index until foundIndex).mapTo(result) {
                            DiffResult.ResultRemoved(
                                old = oldSet[it].comp
                            )
                        }
                        index = foundIndex

                        oldPair = oldSet[index]
                        val diffResult = if (newPair.comp.javaClass == oldPair.comp.javaClass) {
                            DiffResult.ResultSame(
                                old = oldPair.comp,
                                new = newPair.comp,
                                nodes = newPair.nodes
                            )
                        } else {
                            DiffResult.ResultChanged(
                                old = oldPair.comp,
                                new = newPair.comp,
                                nodes = newPair.nodes
                            )
                        }
                        result += diffResult
                        index++
                    } else {
                        // newPair is a new element
                        result += DiffResult.ResultNew(
                            new = newPair.comp,
                            nodes = newPair.nodes
                        )
                    }
                }
            }
        }

        // for all the elements in old that are not in new, mark them as removed
        for (i in index until oldSet.size) {
            result += DiffResult.ResultRemoved(
                old = oldSet[i].comp
            )
        }

        return result
    }

    private fun expandLayer(ctx: RContext, comp: Component, node: RNode): List<Pair<Component, List<RNode>>> {

        val descriptor = node.componentDescriptor

        return when (descriptor) {
            is RComponentDescriptor<*, *> -> {
                createRComponent(ctx, descriptor, comp, node.key).render().flatMap { expandLayer(ctx, comp, it) }
            }
            FragmentDescriptor -> {
                node.children.flatMap { expandLayer(ctx, comp, it) }
            }
            EmptyDescriptor -> emptyList()
            else -> {
                listOf(createComponent(node) to node.children)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createRComponent(ctx: RContext, descriptor: RComponentDescriptor<*, *>, mount: Component, key: String?)
            : RComponent<*, *> {
        if (Renderer.METADATA_COMPONENTS !in mount.metadata) {
            mount.metadata[Renderer.METADATA_COMPONENTS] = mutableListOf<RComponent<*, *>>()
        }
        val compList = mount.metadata[Renderer.METADATA_COMPONENTS] as MutableList<RComponent<*, *>>

        val alreadyExisting = compList.find { it::class.java == descriptor.clazz && !it.mounted && it.key == key }
        val rComponent: RComponent<RProps, RState>

        rComponent = if (alreadyExisting != null) {
            alreadyExisting as RComponent<RProps, RState>
        } else {
            try {
                (descriptor.clazz.newInstance() as RComponent<RProps, RState>).also {
                    compList.add(it)
                    it.key = key
                    it.ctx = ctx
                }
            } catch (e: Exception) {
                throw IllegalStateException("${descriptor.clazz} doesn't have a empty constructor!", e)
            }
        }

        rComponent.componentWillReceiveProps(descriptor.props)
        rComponent.mountPoint = mount
        rComponent.mounted = true
        ctx.mountedComponents.add(rComponent)
        return rComponent
    }

    @Suppress("UNCHECKED_CAST")
    private fun createComponent(node: RNode): Component {
        return node.componentDescriptor.mapToComponent().apply {

            metadata[Renderer.METADATA_KEY] = node.key

            node.listeners.forEach { listener_ ->
                val (clazz, handler) = listener_ as Listener<Event<Component>>
                listenerMap.addListener(clazz, handler)
            }

            node.deferred?.invoke(this)
        }
    }

    private fun merge(ctx: RContext, old: Component, new: Component, childs: List<RNode>): Component {
        if (old.javaClass != new.javaClass) {
            traverse(ctx, new, childs.toFragment())
            return new
        }
        if (old.count() != childs.count()) {
            // Technically childs.count() is not the amount of sub-components what will get generated,
            // because RComponents can generate more than 1 root component
            // This is a complex case to handle and not very common, so I will use the Ostrich algorithm,
            // just assuming that the trees can't merge and remove all local state in the child components
            traverse(ctx, new, childs.toFragment())
            return new
        }

        val strategy = strategies[old.javaClass] ?: DefaultMergeStrategy
        val result = strategy.merge(old, new)

        traverse(ctx, result, childs.toFragment())
        return result
    }

    private fun List<RNode>.toFragment(): RNode {
        return RNode("Fragment", FragmentDescriptor, this)
    }
}