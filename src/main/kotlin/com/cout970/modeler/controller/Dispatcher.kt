package com.cout970.modeler.controller

import com.cout970.modeler.Program
import com.cout970.modeler.controller.injection.DependencyInjector
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.usecases.UseCase
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.gui.COMPUTE
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.util.ITickeable
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.liquidengine.legui.component.Component
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

/**
 * Created by cout970 on 2017/07/17.
 */

interface IDispatcher {
    fun onEvent(key: String, comp: Component?)
}

lateinit var dispatcher: IDispatcher

class Dispatcher : ITickeable, IDispatcher {

    lateinit var state: Program

    init {
        dispatcher = this
    }

    val dependencyInjector = DependencyInjector()
    val functionUseCases: Map<String, KFunction<*>> = findFunctionUseCases()

    private val sideEffects = Collections.synchronizedList(mutableListOf<Pair<String, Deferred<ITask>>>())

    override fun tick() {
        sideEffects.removeAll { (key, job) ->
            if (job.isCompleted) {
                if (job.isCompletedExceptionally) {
                    val e = job.getCompletionExceptionOrNull()!!
                    log(Level.ERROR) { "Unable to run usecase: $key" }
                    e.print()
                    val cause = e.cause

                    val msg = if (cause != null)
                        cause.message ?: cause::class.java.simpleName
                    else e.message ?: e::class.java.simpleName

                    NotificationHandler.push(Notification("Internal error", msg))
                } else {
                    state.taskHistory.processTask(job.getCompleted())
                }
                true
            } else {
                false
            }
        }
    }

    override fun onEvent(key: String, comp: Component?) {
        Profiler.startSection("Dispatcher")
        log(Level.FINEST) { "[Dispatcher] Executing: $key" }

        val useCase = functionUseCases[key]

        if (useCase == null) {
            log(Level.ERROR) { "[Dispatcher] No UseCase found for $key" }
        } else {
            val id = "${useCase::class.simpleName}, ${useCase.name}, key: $key"
            sideEffects.add(id to async(COMPUTE) {
                dependencyInjector.callUseCase(state, comp, useCase.apply { isAccessible = true })
            })
        }
        Profiler.endSection()
    }

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
}