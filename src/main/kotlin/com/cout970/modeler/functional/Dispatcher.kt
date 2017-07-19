package com.cout970.modeler.functional

import com.cout970.modeler.ProgramState
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.functional.injection.DependencyInjector
import com.cout970.modeler.functional.usecases.*
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/17.
 */
class Dispatcher {

    lateinit var state: ProgramState

    val dependencyInjector = DependencyInjector()
    val useCases = listOf(
            NewProject(),
            LoadProject(),
            SaveProject(),
            SaveProjectAs(),
            ImportModel(),
            ExportModel(),
            AddTemplateCube(),
            AddMeshCube()
    )
    val useCasesMap = useCases.associate { it.key to it }

    fun onEvent(key: String, comp: Component?) {
        val useCase = useCasesMap[key] ?: return

        try {
            dependencyInjector.injectDependencies(state, comp, useCase)
            val task = useCase.createTask()
            state.taskHistory.processTask(task)
        } catch (e: Exception) {
            log(Level.ERROR) { "Usable to run usecase: ${useCase::class}" }
            e.print()
        }
    }
}