package com.cout970.modeler.functional.usecases

import com.cout970.modeler.functional.tasks.ITask

/**
 * Created by cout970 on 2017/07/17.
 */

interface IUseCase {

    val key: String

    fun createTask(): ITask
}