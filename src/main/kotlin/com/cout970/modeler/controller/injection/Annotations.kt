package com.cout970.modeler.controller.injection

/**
 * Created by cout970 on 2017/07/19.
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectFromGui(val key: String)