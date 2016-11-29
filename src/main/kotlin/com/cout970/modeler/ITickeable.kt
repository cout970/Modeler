package com.cout970.modeler

/**
 * Created by cout970 on 2016/11/29.
 */
interface ITickeable {

    fun preTick(){}

    fun tick()

    fun postTick(){}
}