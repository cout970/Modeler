package com.cout970.modeler.functional.util

import org.funktionale.option.Option

/**
 * Created by cout970 on 2017/07/19.
 */

fun <T> optionOf(value: T?): Option<T> {
    if (value == null) return Option.None
    return Option.Some(value)
}
