package com.cout970.modeler.controller

import com.cout970.modeler.Program
import com.cout970.modeler.controller.injection.DependencyInjector
import com.cout970.modeler.controller.usecases.IUseCase
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/17.
 */
class Dispatcher {

    lateinit var state: Program

    val dependencyInjector = DependencyInjector()
    val useCasesMap = findUseCases().associate { it.key to it }

    private fun findUseCases(): List<IUseCase> {
        log(Level.FINE) { "[Dispatcher] Searching IUseCases with reflection..." }
        val list = StackOverflowSnippets.getClassesForPackage("com.cout970.modeler.functional.usecases")
        log(Level.FINEST) { "IUseCase classes: $list" }
        val instances = list
                .filter { !it.isInterface && IUseCase::class.java.isAssignableFrom(it) }
                .map { it.constructors.first().newInstance() as IUseCase }
                .toList()
        log(Level.FINE) { "[Dispatcher] Search done, found ${instances.size} classes" }
        return instances
    }

    fun onEvent(key: String, comp: Component?) {
        log(Level.FINEST) { "[Dispatcher] Executing: $key" }
        val useCase = useCasesMap[key] ?: return

        try {
            dependencyInjector.injectDependencies(state, comp, useCase)
            val task = useCase.createTask()
            state.taskHistory.processTask(task)
        } catch (e: Exception) {
            log(Level.ERROR) { "Usable to run usecase: ${useCase::class.simpleName}" }
            e.print()
        }
    }
}