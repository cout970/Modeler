package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask

/**
 * Created by cout970 on 2017/07/17.
 */

interface IUseCase {

    val key: String

    fun createTask(): ITask
}