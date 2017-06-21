package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/06/16.
 */
interface IFutureExecutor {

    fun addToQueue(function: () -> Unit)
}