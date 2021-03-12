package com.cout970.reactive.core

import org.liquidengine.legui.component.Component

object RDebug {

    // Utility function for debug
    fun printTree(node: RNode, prefix: String = "") {
        println("${prefix}Node(${node.key}, ${node.componentDescriptor::class.java.simpleName}){")
        node.children.forEach {
            printTree(it, "$prefix|   ")
        }
        println("$prefix}")
    }

    fun printTree(comp: Component, prefix: String = "") {
        println("${prefix}Node(${comp.getKey()}, ${comp::class.java.simpleName}){")
        comp.childComponents.forEach {
            printTree(it, "$prefix|   ")
        }
        println("$prefix}")
    }

    private fun Component.getKey(): String? = metadata["key"]?.toString()
}