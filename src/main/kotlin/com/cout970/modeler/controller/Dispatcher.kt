package com.cout970.modeler.controller

import com.cout970.modeler.Program
import com.cout970.modeler.controller.injection.DependencyInjector
import com.cout970.modeler.controller.usecases.UseCase
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import org.liquidengine.legui.component.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

/**
 * Created by cout970 on 2017/07/17.
 */
class Dispatcher {

    lateinit var state: Program

    val dependencyInjector = DependencyInjector()
    val functionUseCases: Map<String, KFunction<*>> = findFunctionUseCases()


    private fun findFunctionUseCases(): Map<String, KFunction<*>> {
        log(Level.FINE) { "[Dispatcher] Searching UseCases with reflection..." }
        val list = StackOverflowSnippets.getClassesForPackage(UseCase::class.java.`package`.name)

        log(Level.FINEST) { "UseCase functions: $list" }
        val instances = list
                .flatMap { it.declaredMethods.toList() }
                .filter { it.isAnnotationPresent(UseCase::class.java) }


        log(Level.FINE) { "[Dispatcher] Search done, found ${instances.size} functions" }

        val map = instances.associate { method ->
            val key = method.getAnnotation(UseCase::class.java).key
            val func = method.kotlinFunction!!.apply { isAccessible = true }

            if (func.visibility != KVisibility.PRIVATE) {
                log(Level.DEBUG) { "non-private function: at ${func.javaMethod?.declaringClass?.simpleName}, $func" }
            }

            Pair(key, func)
        }

        if (map.size != instances.size) {
            log(Level.ERROR) { "[Dispatcher] Found duplicated UseCases ids, unique ids: ${map.size}, total usecase: ${instances.size} " }
        }

        return map
    }

    fun checkUseCases() {
        functionUseCases.forEach { _, func ->
            dependencyInjector.checkUseCaseArguments(state, func)
        }
    }

    fun onEvent(key: String, comp: Component?) {
        Profiler.startSection("Dispatcher")
        log(Level.FINEST) { "[Dispatcher] Executing: $key" }

        val useCase = functionUseCases[key]

        if (useCase == null) {
            log(Level.ERROR) { "[Dispatcher] No UseCase found for $key" }
        } else {
            try {
                Profiler.startSection(key)
                val task = dependencyInjector.callUseCase(state, comp, useCase.apply { isAccessible = true })
                Profiler.endSection()
                state.taskHistory.processTask(task)
            } catch (e: Exception) {
                log(Level.ERROR) { "Unable to run usecase: ${useCase::class.simpleName}, ${useCase.name}, key: $key" }
                e.print()
                val cause = e.cause!!
                NotificationHandler.push(Notification("Internal error", cause.message ?: cause::class.java.simpleName))
            }
        }
        Profiler.endSection()
    }
}