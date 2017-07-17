package com.cout970.modeler.functional

import com.cout970.modeler.ProgramState
import com.cout970.modeler.functional.usecases.AddCubeUseCase
import com.cout970.modeler.functional.usecases.IUseCase
import com.cout970.modeler.functional.usecases.IUserEvent
import com.cout970.modeler.functional.usecases.NewProjectUseCase
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/17.
 */
class Dispatcher {

    lateinit var state: ProgramState

    val useCases = listOf(
            AddCubeUseCase(),
            NewProjectUseCase()
    )

    private val useCasesMap = useCases.associate { it.key to it }

    fun onEvent(key: String, caller: Component? = null) {
        val useCase = useCasesMap[key] ?: return
        run(useCase, caller)
    }

    private fun <T : IUserEvent> run(useCase: IUseCase<T>, caller: Component?) {
        val event = useCase.buildEvent(state, caller)
        val task = useCase.processor.processEvent(event)

        state.taskHistory.processTask(task)
    }
}